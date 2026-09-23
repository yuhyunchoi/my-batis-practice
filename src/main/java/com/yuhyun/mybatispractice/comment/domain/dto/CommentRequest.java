package com.yuhyun.mybatispractice.comment.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 500, message = "댓글을 500자 이내로 입력해주세요.")
        String content,
        @NotBlank(message = "작성자는 필수 입니다.")
        @Size(max = 50)
        String writer,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 20, message = "비밀번호는 4-20자입니다.")
        String password
) {
}
