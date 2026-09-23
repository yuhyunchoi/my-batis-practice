package com.yuhyun.mybatispractice.board.service;

import com.yuhyun.mybatispractice.board.domain.dto.*;
import com.yuhyun.mybatispractice.page.PageResponse;

import java.util.List;

public interface BoardService {
    PageResponse<BoardResponse> getAllBoard(BoardSearchCondition condition);
    BoardResponse findByBoardId(Long boardId);
    BoardResponse createBoard(BoardRequest boardRequest);
    BoardResponse updateBoard(Long boardId, BoardUpdateRequest boardRequest);
    void deleteById(Long boardId, BoardDeleteRequest boardDeleteRequest);

}
