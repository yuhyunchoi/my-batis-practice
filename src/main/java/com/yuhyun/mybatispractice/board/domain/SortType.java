package com.yuhyun.mybatispractice.board.domain;

public enum SortType {
    LATEST("BOARD_ID", "DESC"),
    OLDEST("BOARD_ID", "ASC"),
    VIEW("VIEW_COUNT", "DESC");

    private final String column;
    private final String direction;

    SortType(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public String getColumn() {
        return column;
    }

    public String getDirection() {
        return direction;
    }

}
