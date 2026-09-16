package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/boards")
public class BoardController {

    private final BoardService boardService;


    @GetMapping
    public List<BoardResponse> getAllBoards(){
        return boardService.getAllBoard();
    }

    @GetMapping("/{board-id}")
    public BoardResponse getBoardById(@PathVariable(name ="board-id") Long boarId) {
        return boardService.findByBoardId(boarId);
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest boardRequest) {
        BoardResponse created = boardService.createBoard(boardRequest);

        return ResponseEntity.created(URI.create("/v1/api/boards" + created.boardId())).body(created);
    }


}
