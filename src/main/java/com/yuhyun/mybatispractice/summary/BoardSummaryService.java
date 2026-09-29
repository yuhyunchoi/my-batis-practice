package com.yuhyun.mybatispractice.summary;

import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardSummaryService {

    private final SummaryClient summaryClient;
    private final BoardMapper boardMapper;

    @Async
    public void generateSummaryAsync(Long boarId, String content) {
        try {
            log.info("요약 시작 - thread={}", Thread.currentThread().getName());
            summaryClient.summarize(content)
                    .ifPresent(s -> boardMapper.updateSummary(boarId, s));
        } catch (Exception e) {
            log.warn("요약 생성 실패 ", e);
        }
    }
}
