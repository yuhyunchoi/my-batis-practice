package com.yuhyun.mybatispractice.summary;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.SummaryResponse;
import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Transactional
    public Optional<String> regenerate(Long boardId) {
        Board board = boardMapper.findById(boardId);
        if (board == null) {
            throw new BoardNotFoundException("해당 게시글이 존재하지 않습니다.");
        }

        Optional<String> result = summaryClient.summarize(board.getContent());
        result.ifPresent(s -> boardMapper.updateSummary(boardId, s));
        return result;

    }

}
