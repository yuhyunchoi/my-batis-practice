package com.yuhyun.mybatispractice.exception;

public class BoardMismatchException extends RuntimeException {
    public BoardMismatchException(String message) {
        super(message);
    }
}
