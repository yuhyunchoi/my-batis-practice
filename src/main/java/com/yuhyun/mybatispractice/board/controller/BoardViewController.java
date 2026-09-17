package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.domain.dto.BoardUpdateRequest;
import com.yuhyun.mybatispractice.board.service.BoardService;
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

    @GetMapping
    public String listView(Model model) {
        List<BoardResponse> boards = boardService.getAllBoard();

        model.addAttribute("boards", boards);

        return "boards/list";
    }

    @GetMapping("/{board-id}")
    public String detailView(@PathVariable(name = "board-id") Long boardId,
                             Model model) {
        BoardResponse board = boardService.findByBoardId(boardId);
        model.addAttribute("board", board);

        return "boards/detail";
    }

    @GetMapping("/new")
    public String formView() {
        return "boards/form";
    }

    @PostMapping
    public String registerBoard(@ModelAttribute BoardRequest boardRequest) {
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
                              @ModelAttribute BoardUpdateRequest boardUpdateRequest) {
        boardService.updateBoard(boardId, boardUpdateRequest);

        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/{board-id}/delete")
    public String deleteBoard(@PathVariable(name = "board-id") Long boarId) {
        boardService.deleteById(boarId);
        return "redirect:/boards";
    }
}
