package com.yuhyun.mybatispractice.board.service;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;

import java.util.List;

public interface BoardService {
    List<BoardResponse> getAllBoard();
    BoardResponse findByBoardId(Long boardId);
    BoardResponse createBoard(BoardRequest boardRequest);
    int updateBoard(Board board);
    int deleteById(Long boardId);

}
