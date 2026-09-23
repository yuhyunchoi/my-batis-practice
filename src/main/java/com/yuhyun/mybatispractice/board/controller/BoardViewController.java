package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.*;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentDeleteRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentRequest;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentResponse;
import com.yuhyun.mybatispractice.comment.domain.dto.CommentUpdateRequest;
import com.yuhyun.mybatispractice.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping
    public String listView(@ModelAttribute BoardSearchCondition condition, Model model) {
        model.addAttribute("result", boardService.getAllBoard(condition));
        model.addAttribute("condition", condition);
        return "boards/list";
    }

    @GetMapping("/{board-id}")
    public String detailView(@PathVariable(name = "board-id") Long boardId,
                             Model model) {
        BoardResponse board = boardService.findByBoardId(boardId);
        List<CommentResponse> comments = commentService.findCommentsByBoardId(boardId);
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);

        return "boards/detail";
    }

    @GetMapping("/new")
    public String formView() {
        return "boards/form";
    }

    @PostMapping
    public String registerBoard(@Valid @ModelAttribute BoardRequest boardRequest) {
        boardService.createBoard(boardRequest);
        return "redirect:/boards";
    }

    @GetMapping("/{board-id}/edit")
    public String editFormView(@PathVariable(name = "board-id") Long boardId,
                               Model model) {

        model.addAttribute("board", boardService.findByBoardId(boardId));
        return "boards/form";
    }


    @PostMapping("/{board-id}/edit")
    public String updateBoard(@PathVariable(name = "board-id") Long boardId,
                              @Valid @ModelAttribute BoardUpdateRequest boardUpdateRequest) {
        boardService.updateBoard(boardId, boardUpdateRequest);

        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{board-id}/delete")
    public String deleteBoard(@PathVariable(name = "board-id") Long boarId, @Valid @ModelAttribute BoardDeleteRequest boardDeleteRequest) {
        boardService.deleteById(boarId, boardDeleteRequest);
        return "redirect:/boards";
    }

    @PostMapping("/{board-id}/comments")
    public String createComment(@PathVariable(name = "board-id") Long boardId,
                                @ModelAttribute CommentRequest commentRequest) {

        commentService.createComment(boardId, commentRequest);

        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{board-id}/comments/{comment-id}/edit")
    public String updateComment(@PathVariable(name = "board-id") Long boardId,
                                @PathVariable(name = "comment-id") Long commentId,
                                @ModelAttribute CommentUpdateRequest commentUpdateRequest) {

        commentService.updateComment(commentId, commentUpdateRequest);

        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{board-id}/comments/{comment-id}/delete")
    public String deleteComment(@PathVariable(name = "board-id") Long boardId,
                                @PathVariable(name = "comment-id") Long commentId,
                                @ModelAttribute CommentDeleteRequest commentDeleteRequest) {
        commentService.deleteByCommentId(commentId, commentDeleteRequest);

        return "redirect:/boards/" + boardId;
    }

}
