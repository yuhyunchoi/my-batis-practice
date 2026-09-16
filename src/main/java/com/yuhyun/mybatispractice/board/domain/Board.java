package com.yuhyun.mybatispractice.board.domain;

import com.yuhyun.mybatispractice.board.domain.dto.BoardRequest;
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
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;


    public static Board of(BoardRequest request) {
        return new Board(null, request.title(), request.content(), request.writer(), null, null, 0);
    }

}
