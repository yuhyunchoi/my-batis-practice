package com.yuhyun.mybatispractice.board.mapper;

import com.yuhyun.mybatispractice.board.domain.Board;
import com.yuhyun.mybatispractice.board.domain.dto.BoardSearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<Board> findAll(BoardSearchCondition condition);

    long countAll(BoardSearchCondition condition);

    Board findById(Long boardId);

    void increaseViewCount(Long boardId);

    int insert(Board board);

    int update(Board board);

    int deleteById(Long boardId);

    boolean existsById(Long boarId);

}
