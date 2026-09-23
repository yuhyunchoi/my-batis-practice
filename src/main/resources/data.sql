-- 게시글 115건 (10개씩이면 12페이지 → 페이지 블록 동작까지 확인 가능)
INSERT INTO BOARD (TITLE, CONTENT, WRITER, PASSWORD, VIEW_COUNT, CREATED_AT, MODIFIED_AT)
SELECT
    CASE MOD(X, 4)
        WHEN 0 THEN '공지사항 - ' || X || '번 안내'
        WHEN 1 THEN '자유게시판 ' || X || '번 글'
        WHEN 2 THEN '질문있습니다 ' || X
        ELSE '테스트 글 ' || X
        END,
    X || '번째 글의 본문입니다. 페이징과 검색 확인용 데이터입니다.',
    CASE MOD(X, 5)
        WHEN 0 THEN '원이'
        WHEN 1 THEN '리브'
        WHEN 2 THEN '미나미'
        WHEN 3 THEN '메이'
        ELSE '제나'
        END,
    '$2b$12$yH7GLIjKA7avGOgqvkw6luYDVsrojn1UAYGLpTUMaWcs7pMfUoRwu',
    MOD(X * 7, 100),
    DATEADD('HOUR', -X, CURRENT_TIMESTAMP),
    DATEADD('HOUR', -X, CURRENT_TIMESTAMP)
FROM SYSTEM_RANGE(1, 115);

-- 1~5번 글에 댓글 3개씩 (CASCADE 확인용)
INSERT INTO BOARD_COMMENT (BOARD_ID, CONTENT, WRITER, PASSWORD)
SELECT
    MOD(X, 5) + 1,
    X || '번 댓글입니다.',
    CASE MOD(X, 3) WHEN 0 THEN '김도영' WHEN 1 THEN '곽빈' ELSE '윤동희' END,
    '$2b$12$yH7GLIjKA7avGOgqvkw6luYDVsrojn1UAYGLpTUMaWcs7pMfUoRwu'
FROM SYSTEM_RANGE(1, 15);