package com.yuhyun.mybatispractice.comment.service.impl;

import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import com.yuhyun.mybatispractice.comment.domain.Comment;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.mapper.CommentMapper;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import com.yuhyun.mybatispractice.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<CommentResponse> findCommentsByBoardId(Long boardId) {
        List<Comment> comments = commentMapper.findCommentsByBoardId(boardId);

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long boarId, CommentRequest commentRequest) {
        Long parentId = resolveParentId(boarId, commentRequest.parentId());

        if (!boardMapper.existsById(boarId)) {
            throw new BoardNotFoundException("해당 글을 찾을 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(commentRequest.password());

        Comment comment = Comment.of(boarId,
                commentRequest.content(),
                commentRequest.writer(),
                encodedPassword,
                parentId);

        commentMapper.insert(comment);

        Comment saved = commentMapper.findById(comment.getCommentId());
        return CommentResponse.from(saved);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest commentRequest) {
        Comment saved = commentMapper.findById(commentId);

        if (saved == null) {
            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다. " + commentId);
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
    @Transactional
    public void deleteByCommentId(Long commentId, CommentDeleteRequest commentRequest) {
        Comment target = commentMapper.findById(commentId);

        if (target == null) {
            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다. " + commentId);
        }

        if (target.getIsDeleted()) {
            throw new CommentAlreadyDeletedException("해당 댓글은 이미 삭제된 댓글입니다.");
        }

        if (!passwordEncoder.matches(commentRequest.password(), target.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        if (commentMapper.existsByParentId(target.getCommentId())) {
            commentMapper.deleteByIdSoft(target.getCommentId());
            return;
        }

        commentMapper.deleteByIdHard(commentId);
    }

    private Long resolveParentId(Long boardId, Long parentId) {
        if (parentId == null) {
            return null;
        }

        Comment parent = commentMapper.findById(parentId);

        if (parent == null) {
            throw new CommentNotFoundException("답글을 달 댓글이 존재하지 않습니다.");
        }

        if (!Objects.equals(boardId, parent.getBoardId())) {
            throw new BoardMismatchException("현재 답글과 댓글의 게시판 정보가 일치하지 않습니다.");
        }

        return parent.getParentId() != null ? parent.getParentId() : parentId;
    }
}
