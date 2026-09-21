package com.yuhyun.mybatispractice.comment.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long boardId;
    private String content;
    private String writer;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static Comment of(Long boardId, String content, String writer, String password) {
        return new Comment(
                null,
                boardId,
                content,
                writer,
                password,
                null,
                null
        );
    }

}
