package com.yuhyun.mybatispractice.exception;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(String message) {
        super(message);
    }

    public BoardNotFoundException(Long boardId) {
        super(boardId + "번 글을 찾을 수 없습니다.");
    }
}
