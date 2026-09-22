package com.yuhyun.mybatispractice.comment.service.impl;

import com.yuhyun.mybatispractice.board.domain.dto.BoardDeleteRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.comment.domain.Comment;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.mapper.CommentMapper;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.CommentNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceImplTest {

    @Autowired
    CommentService commentService;

    @Autowired
    BoardService boardService;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Long savedBoardId;
    private Long savedCommentId;

    @BeforeEach
    void setUp() {
        savedBoardId = boardService.createBoard(
                new BoardRequest("테스트 제목", "테스트 내용", "최유현", "1234")).boardId();

        savedCommentId = commentService.createComment(
                savedBoardId, new CommentRequest("테스트 댓글", "윤동희", "1234")).commentId();
    }

    @Test
    void 댓글을_등록하면_등록된_댓글이_반환된다() {
        // given
        CommentRequest request = new CommentRequest("새 댓글", "김도영", "1234");

        // when
        CommentResponse result = commentService.createComment(savedBoardId, request);

        // then
        assertThat(result.commentId()).isNotNull();
        assertThat(result.boardId()).isEqualTo(savedBoardId);
        assertThat(result.content()).isEqualTo("새 댓글");
        assertThat(result.writer()).isEqualTo("김도영");
    }

    @Test
    void 저장된_댓글_비밀번호는_평문이_아니다() {
        //when
        Comment saved = commentMapper.findById(savedCommentId);

        //then
        assertThat(saved.getPassword()).isNotEqualTo("1234");
        assertThat(saved.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("1234", saved.getPassword())).isTrue();
    }

    @Test
    void 게시글의_댓글_목록을_조회할_수_있다() {
        // given
        commentService.createComment(savedBoardId,
                new CommentRequest("두 번째 댓글", "김도영", "1234"));//given

        // when
        List<CommentResponse> comments = commentService.findCommentsByBoardId(savedBoardId);

        //then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).content()).isEqualTo("테스트 댓글");
        assertThat(comments.get(1).content()).isEqualTo("두 번째 댓글");;
    }

    @Test
    void 올바른_비밀번호로_댓글을_수정할_수_있다() {
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글", "1234");

        //when
        CommentResponse result = commentService.updateComment(savedCommentId, request);

        //then
        assertThat(result.content()).isEqualTo("수정된 댓글");

        Comment saved = commentMapper.findById(savedCommentId);
        assertThat(saved.getContent()).isEqualTo("수정된 댓글");
        assertThat(saved.getWriter()).isEqualTo("윤동희");
    }

    @Test
    void 틀린_비밀번호로는_댓글을_수정할_수_없다() {
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글", "1234567890");

        //when&then
        assertThatThrownBy(() -> commentService.updateComment(savedCommentId, request))
                .isInstanceOf(PasswordMismatchException.class);

        Comment saved = commentMapper.findById(savedCommentId);
        assertThat(saved.getContent()).isEqualTo("테스트 댓글");
    }

    @Test
    void 없는_댓글을_수정하면_예외가_발생한다() {
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정", "1234");

        //when&then
        assertThatThrownBy(() -> commentService.updateComment(9999L, request))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 올바른_비밀번호로_댓글을_삭제할_수_있다() {
        //given
        CommentDeleteRequest request = new CommentDeleteRequest("1234");

        //when
        commentService.deleteByCommentId(savedCommentId, request);

        //then
        assertThat(commentMapper.findById(savedCommentId)).isNull();
    }

    @Test
    void 틀린_비밀번호로_삭제에_실패하면_댓글이_남아있다() {
        //given
        CommentDeleteRequest request = new CommentDeleteRequest("1234567890");

        // when
        assertThatThrownBy(() -> commentService.deleteByCommentId(savedCommentId, request))
                .isInstanceOf(PasswordMismatchException.class);

        assertThat(commentMapper.findById(savedCommentId)).isNotNull();
    }

    @Test
    void 없는_댓글을_삭제하면_예외가_발생한다() {
        //given
        CommentDeleteRequest request = new CommentDeleteRequest("1234");

        //when
        assertThatThrownBy(() -> commentService.deleteByCommentId(9999L, request))
                .isInstanceOf(CommentNotFoundException.class);

    }

    @Test
    void 게시글을_삭제하면_댓글도_함께_삭제된다() {
        //given
        assertThat(commentService.findCommentsByBoardId(savedBoardId)).hasSize(1);

        //when
        boardService.deleteById(savedBoardId, new BoardDeleteRequest("1234"));

        //then
        assertThat(commentMapper.findById(savedCommentId)).isNull();

    }

    @Test
    void 없는_게시글에_댓글을_달면_예외가_발생한다() {
        //given
        CommentRequest request = new CommentRequest("새 댓글", "곽빈", "1234");

        //when&then
        assertThatThrownBy(() -> commentService.createComment(9999L, request))
                .isInstanceOf(BoardNotFoundException.class);
    }
}