package com.yuhyun.mybatispractice.page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        PageInfo pageInfo
) {
}
