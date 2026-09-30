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
import com.yuhyun.mybatispractice.exception.*;
import com.yuhyun.mybatispractice.summary.BoardSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    BoardSummaryService summaryService;

    private Long savedBoardId;
    private Long savedCommentId;

    @BeforeEach
    void setUp() {
        savedBoardId = boardService.createBoard(
                new BoardRequest("테스트 제목", "테스트 내용", "최유현", "1234")).boardId();

        savedCommentId = commentService.createComment(
                savedBoardId, new CommentRequest("테스트 댓글", "윤동희", "1234", null)).commentId();
    }

    private Long 게시글_생성() {
        return boardService.createBoard(
                new BoardRequest("다른 게시글", "내용", "작성자", "1234")).boardId();
    }

    private Long 답글_생성(Long boardId, Long parentId) {
        return commentService.createComment(
                boardId, new CommentRequest("테스트 답글", "작성자", "1234", parentId)).commentId();
    }

    @Test
    void 댓글을_등록하면_등록된_댓글이_반환된다() {
        // given
        CommentRequest request = new CommentRequest("새 댓글", "김도영", "1234", null);

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
                new CommentRequest("두 번째 댓글", "김도영", "1234", null));

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
        CommentRequest request = new CommentRequest("새 댓글", "곽빈", "1234", null);

        //when&then
        assertThatThrownBy(() -> commentService.createComment(9999L, request))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    void 일반_댓글은_parentId가_null이다() {
        Comment saved = commentMapper.findById(savedCommentId);

        assertThat(saved.getParentId()).isNull();
    }

    @Test
    void 댓글에_답글을_달면_parentId가_저장된다() {
        //given
        Long replyId = 답글_생성(savedBoardId, savedCommentId);

        // then
        Comment savedReply = commentMapper.findById(replyId);
        assertThat(savedReply.getParentId()).isEqualTo(savedCommentId);
    }

    @Test
    void 답글에_답글을_달면_원래_부모에_붙는다	() {
        //given
        Long replyId = 답글_생성(savedBoardId, savedCommentId);

        // when
        Long replyToReplyId = 답글_생성(savedBoardId, replyId);

        //then
        Comment result = commentMapper.findById(replyToReplyId);
        assertThat(result.getParentId()).isEqualTo(savedCommentId);
    }

    @Test
    void 존재하지_않는_댓글에_답글을_달면_예외() {
        assertThatThrownBy(() -> 답글_생성(savedBoardId, 9999L))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 다른_게시글의_댓글에_답글을_달면_예외() {
        // given
        Long otherBoardId = 게시글_생성();

        // when & then
        assertThatThrownBy(() -> 답글_생성(otherBoardId, savedCommentId))
                .isInstanceOf(BoardMismatchException.class);
    }

    @Test
    void 존재하지_않는_댓글을_삭제하면_에외() {
        assertThatThrownBy(() -> commentService.deleteByCommentId(9999L, new CommentDeleteRequest("1234")))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 비밀번호가_틀리면_삭제되지_않는다() {
        assertThatThrownBy(() -> commentService.deleteByCommentId(savedCommentId, new CommentDeleteRequest("1234567890")))
                .isInstanceOf(PasswordMismatchException.class);

        Comment saved = commentMapper.findById(savedCommentId);
        assertThat(saved).isNotNull();
        assertThat(saved.getIsDeleted()).isFalse();
    }

    @Test
    void 답글이_있는_댓글을_삭제하면_삭제_표시만_남는다() {
        Long replyId = 답글_생성(savedBoardId, savedCommentId);

        commentService.deleteByCommentId(savedCommentId, new CommentDeleteRequest("1234"));

        Comment saved = commentMapper.findById(savedCommentId);
        assertThat(saved).isNotNull();
        assertThat(saved.getIsDeleted()).isTrue();
        assertThat(saved.getContent()).isNullOrEmpty();
        assertThat(commentMapper.findById(replyId)).isNotNull();
    }

    @Test
    void 답글이_없는_댓글을_삭제하면_완전히_삭제된다() {
        // when
        commentService.deleteByCommentId(savedCommentId, new CommentDeleteRequest("1234"));

        // then
        assertThat(commentMapper.findById(savedCommentId)).isNull();
    }


    @Test
    void 답글을_삭제하면_완전히_삭제된다() {
        //given
        Long replyId = 답글_생성(savedBoardId, savedCommentId);

        //when
        commentService.deleteByCommentId(replyId, new CommentDeleteRequest("1234"));

        //then
        assertThat(commentMapper.findById(replyId)).isNull();
    }

    @Test
    void 이미_삭제된_댓글을_삭제하면_예외() {
        답글_생성(savedBoardId, savedCommentId);

        commentService.deleteByCommentId(savedCommentId, new CommentDeleteRequest("1234"));

        assertThatThrownBy(() -> commentService.deleteByCommentId(savedCommentId, new CommentDeleteRequest("1234")))
                .isInstanceOf(CommentAlreadyDeletedException.class);
    }

}