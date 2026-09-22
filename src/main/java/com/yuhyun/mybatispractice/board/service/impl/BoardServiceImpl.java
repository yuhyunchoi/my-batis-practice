package com.yuhyun.mybatispractice.board.service.impl;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.BoardDeleteRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
import com.yuhyun.mybatispractice.board.domain.dto.BoardResponse;
import com.yuhyun.mybatispractice.board.domain.dto.BoardUpdateRequest;
import com.yuhyun.mybatispractice.board.mapper.BoardMapper;
import com.yuhyun.mybatispractice.board.service.BoardService;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<BoardResponse> getAllBoard() {
        List<Board> boardList = boardMapper.findAll();

        return boardList.stream()
                .map(BoardResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public BoardResponse findByBoardId(Long boardId) {
        boardMapper.increaseViewCount(boardId);Board target = boardMapper.findById(boardId);


        if (target == null) {
            throw new BoardNotFoundException(boardId);
        }

        return BoardResponse.from(target);
    }

    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest boardRequest) {
        String encodedPassword = passwordEncoder.encode(boardRequest.password());

        Board createdBoard = Board.of(boardRequest.title(),
                boardRequest.content(),
                encodedPassword,
                boardRequest.writer());

        boardMapper.insert(createdBoard);

        Board saved = boardMapper.findById(createdBoard.getBoardId());
        return BoardResponse.from(saved);
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest boardRequest) {
        Board origin = boardMapper.findById(boardId);

        if (!passwordEncoder.matches(boardRequest.password(), origin.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        origin.setTitle(boardRequest.title());
        origin.setContent(boardRequest.content());

        boardMapper.update(origin);

        Board updated = boardMapper.findById(boardId);

        return BoardResponse.from(updated);
    }


    @Override
    @Transactional
    public void deleteById(Long boardId, BoardDeleteRequest boardDeleteRequest) {
        if (boardMapper.findById(boardId) == null) {
            throw new BoardNotFoundException(boardId + "번 글을 삭제할 수 없습니다.");
        }

        if (!passwordEncoder.matches(boardDeleteRequest.password(), boardMapper.findById(boardId).getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        boardMapper.deleteById(boardId);
    }
}
