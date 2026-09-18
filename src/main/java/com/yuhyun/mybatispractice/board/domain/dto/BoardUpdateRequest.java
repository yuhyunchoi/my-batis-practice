package com.yuhyun.mybatispractice.board.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardUpdateRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank
        @Size(min = 4, max = 20, message = "비밀번호는 4~20자로 입력해주세요")
        String password
) {
}
