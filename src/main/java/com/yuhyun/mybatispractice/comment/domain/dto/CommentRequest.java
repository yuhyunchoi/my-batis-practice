package com.yuhyun.mybatispractice.comment.domain.dto;

public record CommentRequest(
        String content,
        String writer,
        String password
) {
}
