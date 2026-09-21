package com.yuhyun.mybatispractice.comment.service;

import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findCommentsByBoardId(Long boarId);

    CommentResponse createComment(Long boardId, CommentRequest commentRequest);

    CommentResponse updateComment(Long commentId, CommentUpdateRequest commentRequest);

    void deleteByCommentId(Long commentId, CommentDeleteRequest commentRequest);
}
