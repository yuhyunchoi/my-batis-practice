package com.yuhyun.mybatispractice.comment.domain.dto;

import com.yuhyun.mybatispractice.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long boardId,
        String content,
        String writer,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getBoardId(),
                comment.getContent(),
                comment.getWriter(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}
