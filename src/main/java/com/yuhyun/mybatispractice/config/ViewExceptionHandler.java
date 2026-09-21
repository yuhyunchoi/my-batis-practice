package com.yuhyun.mybatispractice.config;

import com.yuhyun.mybatispractice.board.controller.BoardViewController;
import com.yuhyun.mybatispractice.exception.BoardNotFoundException;
import com.yuhyun.mybatispractice.exception.CommentNotFoundException;
import com.yuhyun.mybatispractice.exception.PasswordMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice(assignableTypes = BoardViewController.class)
public class ViewExceptionHandler {
    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBoardNotFound(BoardNotFoundException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden(PasswordMismatchException e, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCommentNotFound(CommentNotFoundException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
