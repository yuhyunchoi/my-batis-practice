package com.yuhyun.mybatispractice.exception;

public class CommentAlreadyDeletedException extends RuntimeException {
    public CommentAlreadyDeletedException(String message) {
        super(message);
    }
}
