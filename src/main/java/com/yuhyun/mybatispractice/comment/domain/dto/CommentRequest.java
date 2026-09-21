package com.yuhyun.mybatispractice.comment.domain.dto;

public record CommentRequest(
        Long boardId,
        String content,
        String writer,
        String password
) {
}
