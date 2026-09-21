package com.yuhyun.mybatispractice.comment.service.impl;

import com.yuhyun.mybatispractice.comment.domain.Comment;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.mapper.CommentMapper;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import com.yuhyun.mybatispractice.exception.CommentNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<CommentResponse> findCommentsByBoardId(Long boarId) {
        List<Comment> comments = commentMapper.findCommentsByBoardId(boarId);

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long boarId, CommentRequest commentRequest) {
        String encodedPassword = passwordEncoder.encode(commentRequest.password());

        Comment comment = Comment.of(boarId,
                commentRequest.content(),
                commentRequest.writer(),
                encodedPassword);

        commentMapper.insert(comment);

        Comment saved = commentMapper.findById(comment.getCommentId());
        return CommentResponse.from(saved);
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest commentRequest) {
        Comment saved = commentMapper.findById(commentId);

        if (saved == null) {
            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다." + commentId);
        }

        if (!passwordEncoder.matches(commentRequest.password(), saved.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        saved.setContent(commentRequest.content());
        commentMapper.update(saved);

        Comment updated = commentMapper.findById(commentId);
        return CommentResponse.from(updated);
    }

    @Override
    public void deleteByCommentId(Long commentId, CommentDeleteRequest commentRequest) {
        if (!passwordEncoder.matches(commentRequest.password(), commentMapper.findById(commentId).getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        int affected = commentMapper.deleteById(commentId);

        if (affected == 0) {
            throw new CommentNotFoundException("해당 댓글을 삭제할 수 없습니다.");
        }
    }

}
