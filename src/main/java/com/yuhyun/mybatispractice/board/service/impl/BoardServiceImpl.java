package com.yuhyun.mybatispractice.board.service.impl;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.domain.dto.BoardUpdateRequest;
import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import com.yuhyun.mybatispractice.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;


    @Override
    public List<BoardResponse> getAllBoard() {
        List<Board> boardList = boardMapper.findAll();

        List<BoardResponse> boardResponseList = new ArrayList<>();

        for(Board savedBoard : boardList) {
            BoardResponse response = new BoardResponse(
                    savedBoard.getBoardId(),
                    savedBoard.getTitle(),
                    savedBoard.getContent(),
                    savedBoard.getWriter(),
                    savedBoard.getCreatedAt(),
                    savedBoard.getModifiedAt(),
                    savedBoard.getViewCount());
            boardResponseList.add(response);
        }

        return boardResponseList;
    }

    @Override
    @Transactional
    public BoardResponse findByBoardId(Long boardId) {
        boardMapper.increaseViewCount(boardId);
        Board target = boardMapper.findById(boardId);

        return new BoardResponse(target.getBoardId(),
                target.getTitle(),
                target.getContent(),
                target.getWriter(),
                target.getCreatedAt(),
                target.getModifiedAt(),
                target.getViewCount());
    }

    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest boardRequest) {
        Board createdBoard = Board.of(boardRequest);
        boardMapper.insert(createdBoard);

        Board saved = boardMapper.findById(createdBoard.getBoardId());
        return BoardResponse.from(saved);
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest boardRequest) {
        Board origin = boardMapper.findById(boardId);

        origin.setTitle(boardRequest.title());
        origin.setContent(boardRequest.content());

        boardMapper.update(origin);

        Board updated = boardMapper.findById(boardId);

        return BoardResponse.from(updated);
    }


    @Override
    public void deleteById(Long boardId) {
        int affected = boardMapper.deleteById(boardId);
        if (affected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, boardId + "번 글을 찾을 수 없습니다.");
        }
    }
}
