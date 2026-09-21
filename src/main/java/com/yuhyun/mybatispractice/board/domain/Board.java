package com.yuhyun.mybatispractice.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String password;
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;


    public static Board of(String title, String content, String password, String writer) {
        return new Board(null,
                title,
                content,
                password,
                writer,
                null,
                null,
                0);
    }

}

