package com.yuhyun.mybatispractice.board.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardUpdateRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
