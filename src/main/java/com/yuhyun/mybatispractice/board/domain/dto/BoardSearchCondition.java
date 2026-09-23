package com.yuhyun.mybatispractice.board.domain.dto;

public record BoardSearchCondition(
        String keyword,
        Integer page,
        Integer size
) {

    public BoardSearchCondition {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 10;
    }

    public int offset() {
        return (page - 1) * size;
    }
}
