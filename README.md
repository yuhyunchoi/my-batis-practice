# MyBatis Practice Board

MyBatis와 Spring Boot를 익히기 위해 만든 게시판 애플리케이션.
REST API와 서버 사이드 렌더링(Thymeleaf) 두 가지 방식으로 구현했다.

## 기술 스택

- Java 21
- Spring Boot 4.0.8
- MyBatis 3.5.19
- H2 Database (in-memory)
- Thymeleaf
- Ollama (로컬 LLM, 선택)

## 주요 기능

### 게시글
- CRUD
- 비밀번호 기반 수정/삭제 (BCrypt 해싱)
- 조회수
- 제목 검색
- 페이징 (블록 단위 페이지 네비게이션)
- 정렬 (최신순 / 오래된순 / 조회수순)

### 댓글
- 게시글별 댓글 CRUD
- 비밀번호 기반 수정/삭제

### AI 요약
- 게시글 등록 시 로컬 LLM(Ollama)으로 본문을 요약
- `@Async`로 비동기 처리하여 등록 응답을 지연시키지 않음
- 요약 실패 시에도 게시글 등록은 정상 완료

## 실행 방법

```bash
./mvnw spring-boot:run
```

http://localhost:8080/boards

H2 인메모리 DB를 쓰므로 별도 설치가 필요 없고, 시작 시 샘플 데이터 115건이 자동으로 들어간다.

### AI 요약을 쓰려면 (선택)

[Ollama](https://ollama.com) 설치 후:

```bash
ollama pull qwen2.5:3b
```

`application.yaml`에서 모델을 바꿀 수 있다.

```yaml
ollama:
  base-url: http://localhost:11434
  model: qwen2.5:3b
```

Ollama가 없어도 애플리케이션은 정상 동작한다. 요약만 생성되지 않는다.

## 프로젝트 구조

```
board/
  controller/   BoardController(REST), BoardViewController(Thymeleaf)
  service/      BoardService, BoardServiceImpl
  mapper/       BoardMapper (+ resources/mapper/BoardMapper.xml)
  domain/       Board, SortType
  dto/          BoardCreateRequest, BoardResponse, BoardSearchCondition
comment/        게시글과 동일한 구조
summary/        SummaryClient(interface), OllamaSummaryClient, BoardSummaryService
page/           PageInfo, PageResponse
exception/      ApiExceptionHandler
```

## API

| Method | URL | 설명 |
|---|---|---|
| GET | `/v1/api/boards` | 목록 (검색·페이징·정렬) |
| GET | `/v1/api/boards/{id}` | 상세 |
| POST | `/v1/api/boards` | 등록 |
| PATCH | `/v1/api/boards/{id}` | 수정 |
| DELETE | `/v1/api/boards/{id}` | 삭제 |

<댓글 API도 같은 형식으로 추가>

## 테스트

```bash
./mvnw test
```

- 서비스: `@SpringBootTest` 기반 통합 테스트
- 컨트롤러: `@WebMvcTest` + `@MockitoBean` 슬라이스 테스트
- 요약 서비스: Mockito 단위 테스트 (외부 LLM 호출 없이 검증)

## 기록

작업하면서 막혔던 것과 알게 된 것들.

### <`@Async`와 트랜잭션 경계>

글 등록에 LLM 요약을 붙이면서 응답이 15초씩 걸렸다. 사용자가 기다릴 이유가 없는 작업이라 비동기로 처리하기로 했다.

주의할 점은 같은 클래스 안에서 호출하면 `@Async`가 작동하지 않는다는 것이다.
스프링이 프록시로 가로채는 구조라 `this.method()`는 프록시를 거치지 않기 때문이다.
`@Transactional`과 같은 구조라, 요약 로직을 별도 빈으로 분리했다.

트랜잭션은 `ThreadLocal`에 보관되어 스레드에 묶여있다. 다른 스레드로 넘어가면 부모의 트랜잭션은 따라가지 않고, 새 커넥션으로 완전히 별개의 트랜잭션이 시작된다.

그래서 글 INSERT가 커밋되기 전에 비동기 쪽이 UPDATE를 시도할 수 있다. 커밋전 데이터는 다른 커넥션에서 보이지 않으므로 `0 rows affected`로 끝난다.

지금은 LLM이 수 초 이상 걸려 우연히 순서가 맞을 뿐이고, 요약이 빨라지면
에러 없이 요약만 누락된다. `@TransactionalEventListener(phase = AFTER_COMMIT)`으로
커밋 이후에 실행시키는 방법이 있으나, 개인 연습 범위에선 적용하지 않고
한계를 인지한 채로 두었다.


### <겉 에러 메시지에 속지 않기>

요약이 간헐적으로 실패했는데 메시지는 이랬다.
```text
RestClientException: Error while extracting response for type [java.util.Map<?, ?>]
and content type [application/octet-stream]
```
응답 형식 문제로 보고 `Accept'/'Content-Type` 헤더를 손보고, 응답을 `String`으로 받아 직접 파싱하는 방법까지 시도했지만 증상은 그대로였다.
스택트레이스 맨 아래 `Caused by`를 따라가니 실제 원인은 전혀 다른 것이었다.

```text
SocketTimeoutException: Read timed out
at HttpClient.parseHTTPHeader ← 응답 헤더를 읽다가 타임아웃
at SimpleClientHttpResponse.getHeaders
at DefaultRestClient.getContentType ← 헤더가 없으니 Content-Type도 없음
at readWithMessageConverters ← 기본값 octet-stream → "변환기가 없다"
```

타임 아웃이 Content-Type 조회 시점에 터지면서, 스프링이 기본 값을 채워넣고 "이 타입을 반환할 수 없다"는 엉뚱한 메시지로 포장한 것이었다.
겉 메시지만 보고 추축으로 고치는 동안 잘못된 수정도 여러번 했다.

타임 아웃을 120초로 늘렸는데로 증상이 같길래 로그 시작을 비교해보니 21초 만에 실패하고 있었다. `RestClient`는 생성자에서 한 번만 만들어지므로, 코드를 고쳐도 재시작 전까지는 옛 설정이 그대로 살아있었던 것이다.


**남긴 것**
- 예외는 메시지만이 아니라 통째로 로깅한다. `log.warn("실패", e)`
- 설정에 의존하는 컴포넌트는 초기화 시 설정값을 로그로 남긴다
- 실패 경로에도 소요 시간을 찍는다. 21초인지 120초인지가 원인을 갈랐다
- 원인을 모른 채 고치지 않는다. 재보면 5분이면 끝날 일이었다

## 앞으로

- [ ] 대댓글
- [ ] 검색 타입 선택 (제목/내용/작성자)
- [ ] 요약 재생성 버튼
- [ ] <...>