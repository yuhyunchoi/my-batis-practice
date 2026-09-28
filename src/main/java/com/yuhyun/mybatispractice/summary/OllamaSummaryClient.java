package com.yuhyun.mybatispractice.summary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class OllamaSummaryClient implements SummaryClient {

    private static final String PROMPT = """
            다음 글을 한글 세 문장으로 간단하게 요약해줘. 요약문만 출력하고 다른 말은 하지마 \n\n
            """;
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(120);

    private final RestClient restClient;
    private final String model;

    public OllamaSummaryClient(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(READ_TIMEOUT);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
        this.model = model;
        log.info("Ollama 클라이언트 초기화 - model={}, readTimeout={}s", model, READ_TIMEOUT);
    }

    @Override
    public Optional<String> summarize(String content) {
        try {
            Map<?, ?> response = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", model,
                            "prompt", PROMPT + content,
                            "stream", false,
                            "keep_alive", "30m",
                            "options", Map.of(
                                    "num_predict", 300,
                                    "temperature", 0.3
                            )
                    ))
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("response") == null) {
                log.warn("요약 응답이 비어있습니다.");
                return Optional.empty();
            }

            String summary = ((String) response.get("response")).trim();
            return summary.isBlank() ? Optional.empty() : Optional.of(summary);

        } catch (Exception e) {
            log.warn("요약 생성 실패: ", e);
            return Optional.empty();
        }
    }
}
