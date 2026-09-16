package com.yuhyun.mybatispractice.board.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String writer
) {

}
