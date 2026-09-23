package com.yuhyun.mybatispractice.comment.controller;

import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{board-id}")
    public List<CommentResponse> getCommentsByBoardId(@PathVariable(name = "board-id") Long boarId) {
        return commentService.findCommentsByBoardId(boarId);
    }

    @PostMapping("/{board-id}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable(name = "board-id") Long boardId,
                                                         @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse response = commentService.createComment(boardId, commentRequest);

        return ResponseEntity.created(URI.create("/v1/api/comments/" + response.commentId())).body(response);
    }

    @PutMapping("/{comment-id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable(name = "comment-id") Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest commentRequest) {
        CommentResponse updated = commentService.updateComment(commentId, commentRequest);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "comment-id") Long commentId,
                                              @Valid @RequestBody CommentDeleteRequest request) {
        commentService.deleteByCommentId(commentId, request);

        return ResponseEntity.noContent().build();
    }
}
