package com.yuhyun.mybatispractice.board.service;

import com.yuhyun.mybatispractice.board.domain.dto.BoardDeleteRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.domain.dto.BoardUpdateRequest;

import java.util.List;

public interface BoardService {
    List<BoardResponse> getAllBoard();
    BoardResponse findByBoardId(Long boardId);
    BoardResponse createBoard(BoardRequest boardRequest);
    BoardResponse updateBoard(Long boardId, BoardUpdateRequest boardRequest);
    void deleteById(Long boardId, BoardDeleteRequest boardDeleteRequest);

}
