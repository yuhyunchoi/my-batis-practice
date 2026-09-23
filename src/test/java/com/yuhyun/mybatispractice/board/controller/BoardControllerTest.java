package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.BoardDeleteRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.domain.dto.BoardUpdateRequest;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import com.yuhyun.mybatispractice.page.PageInfo;
import com.yuhyun.mybatispractice.page.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BoardService boardService;


    private BoardResponse response;

    @BeforeEach
    void setUp() {
        response = new BoardResponse(
                1L, "제목", "내용", "최유현",
                LocalDateTime.now(), LocalDateTime.now(),0);
    }

    @Test
    void 글을_조회하면_200과_글_정보가_반환된다() throws Exception {
        given(boardService.findByBoardId(1L)).willReturn(response);

        mockMvc.perform(get("/v1/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"));
    }

    @Test
    void 없는_글을_조회하면_404가_반환된다() throws Exception {
        // given
        given(boardService.findByBoardId(9999L))
                .willThrow(new BoardNotFoundException("9999번 글을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/v1/api/boards/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("9999번 글을 찾을 수 없습니다."));
    }

    @Test
    void 글을_등록하면_201과_Location_헤더가_반환된다() throws Exception {
        //given
        BoardRequest request = new BoardRequest("새 제목", "새 내용", "새 작성자", "1234");

        BoardResponse response = new BoardResponse(
                2L, "새 제목", "새 내용", "새 작성자",
                LocalDateTime.now(), LocalDateTime.now(), 0);

        given(boardService.createBoard(any())).willReturn(response);

        //when&then
        mockMvc.perform(post("/v1/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/api/boards/2"))
                .andExpect(jsonPath("$.boardId").value(2));

    }

    @Test
    void 제목이_비어있으면_400이_반환된다() throws Exception {
        //given
        BoardRequest request = new BoardRequest("", "새 내용", "새 작성자", "1234");

        // when & then
        mockMvc.perform(post("/v1/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void 전체_조회하면_200과_배열이_반환된다() throws Exception {
        // given
        List<BoardResponse> boards = List.of(
                new BoardResponse(2L, "두 번째", "내용", "최유현",
                        LocalDateTime.now(), LocalDateTime.now(),5),
                new BoardResponse(1L, "첫 번째", "내용", "홍길동",
                        LocalDateTime.now(), LocalDateTime.now(),3)
        );
        PageResponse<BoardResponse> pageResponse =
                new PageResponse<>(boards, PageInfo.of(1, 10, 2));

        given(boardService.getAllBoard(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/v1/api/boards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("두 번째"))
                .andExpect(jsonPath("$.pageInfo.totalCount").value(2))
                .andExpect(jsonPath("$.pageInfo.totalPages").value(1));

    }

    @Test
    void 수정하면_200과_수정된_글이_반환된다() throws Exception {
        //given
        BoardUpdateRequest request = new BoardUpdateRequest("제목 수정", "내용 수정", "1234");

        BoardResponse response = new BoardResponse(
                1L, "제목 수정", "내용 수정", "최유현",
                LocalDateTime.now(), LocalDateTime.now(),3);

        given(boardService.updateBoard(eq(1L), any())).willReturn(response);

        //when&then
        mockMvc.perform(put("/v1/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목 수정"))
                .andExpect(jsonPath("$.content").value("내용 수정"))
                .andExpect(jsonPath("$.writer").value("최유현"));

    }

    @Test
    void 수정_시_비밀번호가_틀리면_403이_반환된다() throws Exception {
        // given
        BoardUpdateRequest request = new BoardUpdateRequest("제목 수정", "내용 수정", "1234567890");

        given(boardService.updateBoard(eq(1L), any()))
                .willThrow(new PasswordMismatchException("비밀번호가 일치하지 않습니다."));

        // when & then
        mockMvc.perform(put("/v1/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 삭제하면_204가_반환된다() throws Exception {
        // given
        BoardDeleteRequest request = new BoardDeleteRequest("1234");
        willDoNothing().given(boardService).deleteById(eq(1L), any());

        // when & then
        mockMvc.perform(delete("/v1/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 삭제_시_비밀번호가_틀리면_403이_반환된다() throws Exception {
        //given
        BoardDeleteRequest request = new BoardDeleteRequest("1234567890");

        willThrow(new PasswordMismatchException("비밀번호가 일치하지 않습니다."))
                .given(boardService).deleteById(eq(1L), any());

        // when & then
        mockMvc.perform(delete("/v1/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 잘못된_형식의_id로_조회하면_400이_반환된다() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/api/boards/abc"))
                .andExpect(status().isBadRequest());
    }

}