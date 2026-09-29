package com.yuhyun.mybatispractice.summary;

import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardSummaryServiceTest {

    @Mock
    SummaryClient summaryClient;

    @Mock
    BoardMapper boardMapper;

    @InjectMocks
    BoardSummaryService boardSummaryService;



    @Test
    void 요약_성공_시_저장된다() {
        // given
        given(summaryClient.summarize("본문")).willReturn(Optional.of("요약문"));

        // when
        boardSummaryService.generateSummaryAsync(1L, "본문");

        // then
        verify(boardMapper).updateSummary(1L, "요약문");
    }

    @Test
    void 요약_실패_시_저장하지_않는다() {
        // given
        given(summaryClient.summarize("본문")).willReturn(Optional.empty());

        // when
        boardSummaryService.generateSummaryAsync(1L, "본문");

        // then
        verify(boardMapper, never()).updateSummary(any(), any());
    }

    @Test
    void 클라이언트가_예외를_던져도_밖으로_안_샌다() {
        // given
        given(summaryClient.summarize("본문")).willThrow(new RuntimeException("요약 실패"));

        // when & then
        assertThatCode(() -> boardSummaryService.generateSummaryAsync(1L, "본문"))
                .doesNotThrowAnyException();

        verify(boardMapper, never()).updateSummary(any(), any());;
    }

    @Test
    void DB_저장이_실패해도_밖으로_안_샌다() {
        // given
        given(summaryClient.summarize("본문")).willReturn(Optional.of("요약문"));
        willThrow(new RuntimeException("DB 저장 실패"))
                .given(boardMapper).updateSummary(any(), any());

        // when & then
        assertThatCode(() -> boardSummaryService.generateSummaryAsync(1L, "본문"))
                .doesNotThrowAnyException();

        verify(boardMapper).updateSummary(1L, "요약문");
    }
}