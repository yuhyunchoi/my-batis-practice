package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardViewController.class)
class BoardViewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BoardService boardService;

    @MockitoBean
    CommentService commentService;

    private BoardResponse boardResponse;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        boardResponse = new BoardResponse(1L, "제목", "내용", "최유현",
                LocalDateTime.now(), LocalDateTime.now(), 5);
        commentResponse = new CommentResponse(
                7L, 1L, "댓글 내용", "홍길동",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void 목록을_요청하면_boards가_모델에_담긴다() throws Exception {
        //given
        given(boardService.getAllBoard()).willReturn(List.of(boardResponse));

        //when&given
        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("boards"));
    }

    @Test
    void 상세를_요청하면_board와_comments가_모델에_담긴다() throws Exception {
        //given
        given(boardService.findByBoardId(1L)).willReturn(boardResponse);
        given(commentService.findCommentsByBoardId(1L)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/boards/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attributeExists("board", "comments"));
    }

    @Test
    void 댓글을_등록하면_해당_게시글_상세로_리다이렉트된다() throws Exception {
        //when&then
        mockMvc.perform(post("/boards/1/comments")
                        .param("content", "댓글 내용")
                        .param("writer", "최유현")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));

    }

    @Test
    void 없는_글을_조회하면_목록으로_리다이렉트된다() throws Exception {
        //given
        given(boardService.findByBoardId(9999L))
                .willThrow(new BoardNotFoundException("9999번 글을 찾을 수 없습니다."));

        //when&then
        mockMvc.perform(get("/boards/9999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("status", 404))
                .andExpect(model().attribute("message", "9999번 글을 찾을 수 없습니다."));

    }

    @Test
    void 글을_등록하면_목록으로_리다이렉트된다() throws Exception {
        // when & then
        mockMvc.perform(post("/boards")
                        .param("title", "새 글")
                        .param("content", "내용")
                        .param("writer", "김도영")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));
    }

    @Test
    void 글을_수정하면_상세로_리다이렉트된다() throws Exception {
        //when&then
        mockMvc.perform(post("/boards/1/edit")
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));

    }

    @Test
    void 글을_삭제하면_목록으로_리다이렉트된다() throws Exception {
        //when&then
        mockMvc.perform(post("/boards/1/delete")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));
    }

    @Test
    void 댓글을_수정하면_해당_게시글_상세로_리다이렉스된다() throws Exception {
        //when&then
        mockMvc.perform(post("/boards/1/comments/7/edit")
                        .param("content", "수정된 댓글")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));
    }

    @Test
    void 댓글을_삭제하면_해당_게시글_상세로_리다이렉트된다() throws Exception {
        //when&then
        mockMvc.perform(post("/boards/1/comments/7/delete")
                        .param("1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));

    }

    @Test
    void 글쓰기_폼을_요청하면_form이_반환된다() throws Exception {
        // when & then
        mockMvc.perform(get("/boards/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"));
    }

    @Test
    void 수정_폼을_요청하면_board가_모델에_담긴다() throws Exception {
        // given
        given(boardService.findByBoardId(1L)).willReturn(boardResponse);

        // when & then
        mockMvc.perform(get("/boards/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attribute("board", boardResponse));
    }
}