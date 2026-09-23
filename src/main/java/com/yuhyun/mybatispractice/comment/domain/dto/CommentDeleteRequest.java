package com.yuhyun.mybatispractice.comment.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentDeleteRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 20, message = "비밀번호는 4-20자입니다.")
        String password
) {
}
