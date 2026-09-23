package com.yuhyun.mybatispractice.board.service.impl;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.*;
import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import com.yuhyun.mybatispractice.page.PageResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardServiceImplTest {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardMapper boardMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Long savedBoardId;

    @BeforeEach
    void setUp() {
        BoardRequest request =
                new BoardRequest("테스트 제목",
                        "테스트 내용",
                        "최유현",
                        "1234");
        savedBoardId = boardService.createBoard(request).boardId();
    }

    @Test
    void 글을_등록하면_등록된_글이_반환된다() {
        //given
        BoardRequest request = new BoardRequest(
                "제목",
                "내용",
                "작성자",
                "1234"
        );

        //when
        BoardResponse result = boardService.createBoard(request);

        //then
        assertThat(result.boardId()).isNotNull();
        assertThat(result.title()).isEqualTo("제목");
        assertThat(result.content()).isEqualTo("내용");
        assertThat(result.writer()).isEqualTo("작성자");
        assertThat(result.viewCount()).isEqualTo(0);
    }

    @Test
    void 저장된_비밀번호는_평문이_아니다() {
        //when
        Board saved = boardMapper.findById(savedBoardId);

        //then
        assertThat(saved.getPassword()).isNotEqualTo("1234");
        assertThat(saved.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("1234", saved.getPassword())).isTrue();
    }

    @Test
    void  조회하면_조회수가_1_증가한다() {
        //given
        Board saved = boardMapper.findById(savedBoardId);

        //when
        BoardResponse result = boardService.findByBoardId(savedBoardId);

        //then
        assertThat(result.viewCount()).isEqualTo(saved.getViewCount() + 1);
    }

    @Test
    void 없는_글을_조회하면_예외가_발생한다() {
        // given
        Long boardId = 9999L;

        //when&then
        assertThatThrownBy(() -> boardService.findByBoardId(boardId))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    void 올바른_비밀번호로_수정할_수_있다() {
        //given
        BoardUpdateRequest request = new BoardUpdateRequest(
                "제목 수정",
                "내용 수정",
                "1234");

        //when
        BoardResponse updated = boardService.updateBoard(savedBoardId, request);

        //then
        assertThat(updated.title()).isEqualTo(request.title());
        assertThat(updated.content()).isEqualTo(request.content());
    }

    @Test
    void 틀린_비밀번호로는_수정할_수_없다() {
        //given
        BoardUpdateRequest request = new BoardUpdateRequest(
                "제목 수정",
                "내용 수정",
                "1234567890" // 틀린 비밀번호
        );

        //when
        assertThatThrownBy(() -> boardService.updateBoard(savedBoardId, request))
                .isInstanceOf(PasswordMismatchException.class);

        Board saved = boardMapper.findById(savedBoardId);
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    void 수정해도_작성자는_바뀌지_않는다() {
        //given
        BoardUpdateRequest request = new BoardUpdateRequest(
                "제목 수정",
                "내용 수정",
                "1234"
        );

        //when
        BoardResponse result = boardService.updateBoard(savedBoardId, request);

        //then
        assertThat(result.writer()).isEqualTo("최유현");
        assertThat(result.title()).isEqualTo("제목 수정");
        assertThat(result.content()).isEqualTo("내용 수정");
    }

    @Test
    void 수정하면_수정일시가_갱신된다() {
        //given
        LocalDateTime before = boardMapper.findById(savedBoardId).getModifiedAt();

        BoardUpdateRequest request = new BoardUpdateRequest(
                "제목 수정",
                "내용 수정",
                "1234"
        );

        //when
        BoardResponse result = boardService.updateBoard(savedBoardId, request);

        //then
        assertThat(result.modifiedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void 올바른_비밀번호로_삭제할_수_있다() {
        //given
        BoardDeleteRequest request = new BoardDeleteRequest("1234");

        //when
        boardService.deleteById(savedBoardId, request);

        //then
        assertThat(boardMapper.findById(savedBoardId)).isNull();
    }

    @Test
    void 틀린_비밀번호로_삭제에_실패하면_글이_남아있다() {
        //given
        BoardDeleteRequest request = new BoardDeleteRequest("123456789");

        //when&then
        assertThatThrownBy(() -> boardService.deleteById(savedBoardId, request))
                .isInstanceOf(PasswordMismatchException.class);

        assertThat(boardMapper.findById(savedBoardId)).isNotNull();
    }

    @Test
    void 없는_글을_삭제하면_예외가_발생한다() {
        // given
        BoardDeleteRequest request = new BoardDeleteRequest("1234");

        // when & then
        assertThatThrownBy(() -> boardService.deleteById(9999L, request))
                .isInstanceOf(BoardNotFoundException.class);
    }


    @Test
    void 전체_조회는_최신글이_먼저_나온다() {
        // given
        BoardResponse newer = boardService.createBoard(
                new BoardRequest("나중 글", "내용", "작성자", "1234"));
        BoardSearchCondition condition = new BoardSearchCondition(null, 1, 10);
        // when
        PageResponse<BoardResponse> result = boardService.getAllBoard(condition);


        // then
        assertThat(result.content().get(0).boardId()).isEqualTo(newer.boardId());
    }

    @Test
    void 검색하면_전체건수도_검색조건을_따른다() {
        //given
        boardService.createBoard(new BoardRequest("공지1", "내용", "최준용", "1234"));
        boardService.createBoard(new BoardRequest("공지2", "내용", "김진욱", "1234"));

        //when
        PageResponse<BoardResponse> result
                = boardService.getAllBoard(new BoardSearchCondition("공지", 1, 10));

        //then
        assertThat(result.content()).hasSize(2);
        assertThat(result.pageInfo().totalCount()).isEqualTo(2);
    }

}