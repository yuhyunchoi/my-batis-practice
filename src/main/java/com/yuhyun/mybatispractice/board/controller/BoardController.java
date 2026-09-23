package com.yuhyun.mybatispractice.board.controller;

import com.yuhyun.mybatispractice.board.domain.dto.*;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.page.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/boards")
public class BoardController {

    private final BoardService boardService;


    @GetMapping
    public PageResponse<BoardResponse> getAllBoards(@ModelAttribute BoardSearchCondition condition) {
        return boardService.getAllBoard(condition);
    }

    @GetMapping("/{board-id}")
    public BoardResponse getBoardById(@PathVariable(name ="board-id") Long boarId) {
        return boardService.findByBoardId(boarId);
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest boardRequest) {
        BoardResponse created = boardService.createBoard(boardRequest);

        return ResponseEntity.created(URI.create("/v1/api/boards/" + created.boardId())).body(created);
    }

    @PutMapping("/{board-id}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable(name = "board-id") Long boardId,@Valid @RequestBody BoardUpdateRequest boardRequest) {
        BoardResponse updated = boardService.updateBoard(boardId, boardRequest);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable(name = "board-id") Long boarId,
                                            @Valid @RequestBody BoardDeleteRequest boardDeleteRequest) {
        boardService.deleteById(boarId, boardDeleteRequest);

        return ResponseEntity.noContent().build();
    }


}
