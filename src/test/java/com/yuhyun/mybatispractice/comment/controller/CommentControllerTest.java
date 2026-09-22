package com.yuhyun.mybatispractice.comment.controller;

import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.CommentNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CommentService commentService;

    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        commentResponse = new CommentResponse(
                7L, 1L, "댓글 내용", "조병현",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void 게시글의_댓글_목록을_조회하면_200과_배열이_반환된다() throws Exception {
        //given
        given(commentService.findCommentsByBoardId(eq(1L))).willReturn(List.of(commentResponse));

        //when&then
        mockMvc.perform(get("/v1/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].commentId").value(7))
                .andExpect(jsonPath("$[0].content").value("댓글 내용"));

    }

    @Test
    void 댓글을_등록하면_201과_Location_헤더가_반환된다() throws Exception {
        // given
        CommentRequest request = new CommentRequest("댓글 내용", "홍길동", "1234");
        given(commentService.createComment(eq(1L), any())).willReturn(commentResponse);

        // when & then
        mockMvc.perform(post("/v1/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/api/comments/7"))
                .andExpect(jsonPath("$.commentId").value(7));
    }

    @Test
    void 없는_게시글에_댓글을_달면_404가_반환된다() throws Exception {
        // given
        CommentRequest request = new CommentRequest("댓글 내용", "노시환", "1234");
        given(commentService.createComment(eq(9999L), any()))
                .willThrow(new BoardNotFoundException("해당 글을 찾을 수 없습니다."));

        //when&then
        mockMvc.perform(post("/v1/api/comments/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 댓글을_수정하면_200과_수정된_댓글이_반환된다() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글", "1234");

        CommentResponse updated = new CommentResponse(
                7L, 1L, "수정된 댓글", "조병현",
                LocalDateTime.now(), LocalDateTime.now());

        given(commentService.updateComment(eq(7L), any())).willReturn(updated);

        // when & then
        mockMvc.perform(put("/v1/api/comments/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(7))
                .andExpect(jsonPath("$.content").value("수정된 댓글"))
                .andExpect(jsonPath("$.writer").value("조병현"));
    }

    @Test
    void 없는_댓글을_수정하면_404가_반환된다() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글", "1234");
        given(commentService.updateComment(eq(9999L), any()))
                .willThrow(new CommentNotFoundException("해당 댓글을 찾을 수 없습니다. 9999"));

        //when&then
        mockMvc.perform(put("/v1/api/comments/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())          // ← 이 줄 추가
                .andExpect(status().isNotFound());
    }

    @Test
    void 수정_시_비밀번호가_틀리면_403이_반환된다() throws Exception {
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글", "1234567890");
        given(commentService.updateComment(eq(7L), any()))
                .willThrow(new PasswordMismatchException("비밀번호가 일치하지 않습니다."));

        //when&then
        mockMvc.perform(put("/v1/api/comments/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 댓글을_삭제하면_204가_반환된다() throws Exception {
        //given
        CommentDeleteRequest request = new CommentDeleteRequest("1234");
        willDoNothing().given(commentService).deleteByCommentId(eq(7L), any());

        //when&then
        mockMvc.perform(delete("/v1/api/comments/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 삭제_시_비밀번호가_틀리면_403이_반환된다() throws Exception {
        //given
        CommentDeleteRequest request = new CommentDeleteRequest("1234567890");
        willThrow(new PasswordMismatchException("비밀번호가 일치하지 않습니다."))
                .given(commentService).deleteByCommentId(eq(7L), any());

        //when&then
        mockMvc.perform(delete("/v1/api/comments/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}