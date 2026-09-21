package com.yuhyun.mybatispractice.comment.controller;

import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("{board-id}")
    public List<CommentResponse> getCommentsByBoardId(@PathVariable(name = "board-id") Long boarId) {
        return commentService.findCommentsByBoardId(boarId);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse response = commentService.createComment(commentRequest);

        return ResponseEntity.created(URI.create("/v1/api/comment" + response.commentId())).body(response);
    }

    @PutMapping("/{comment-id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable(name = "comment-id") Long commentId,
                                                         @RequestBody CommentUpdateRequest commentRequest) {
        CommentResponse updated = commentService.updateComment(commentId, commentRequest);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "comment-id") Long commentId,
                                              @RequestBody CommentDeleteRequest request) {
        commentService.deleteByCommentId(commentId, request);

        return ResponseEntity.noContent().build();
    }
}
