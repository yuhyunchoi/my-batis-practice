package com.yuhyun.mybatispractice.board.domain.dto;

import com.yuhyun.mybatispractice.board.domain.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long boardId,
        String title,
        String content,
        String writer,
        LocalDateTime createAt,
        LocalDateTime modifiedAt,
        int viewCount
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getBoardId(), board.getTitle(), board.getContent(), board.getWriter(), board.getCreatedAt(), board.getModifiedAt(), board.getViewCount()
        );
    }
}
