package com.yuhyun.mybatispractice.summary;

import java.util.Optional;

public interface SummaryClient {
    Optional<String> summarize(String content);
}
