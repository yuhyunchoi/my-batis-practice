package com.yuhyun.mybatispractice.comment.domain.dto;

public record CommentUpdateRequest(
        String content,
        String password
) {
}
