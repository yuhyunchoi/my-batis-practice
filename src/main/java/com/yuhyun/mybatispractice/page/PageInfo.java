package com.yuhyun.mybatispractice.page;

public record PageInfo(
        int page,          // 현재 페이지
        int size,          // 한 페이지 건수
        long totalCount,   // 전체 건수
        int totalPages,    // 전체 페이지 수
        int startPage,     // 하단 블록 시작 (예: 11)
        int endPage,       // 하단 블록 끝   (예: 20)
        boolean hasPrev,
        boolean hasNext
) {
    private static final int BLOCK_SIZE = 10;

    public static PageInfo of(int page, int size, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        if (totalPages == 0) {
            totalPages = 1;
        }

        int startPage = ((page - 1) / BLOCK_SIZE) * BLOCK_SIZE + 1;
        int endPage = startPage + BLOCK_SIZE - 1;
        if (endPage > totalPages) {
            endPage = totalPages;
        }

        boolean hasPrev = startPage > 1;
        boolean hasNext = endPage < totalPages;

        return new PageInfo(page, size, totalCount, totalPages,
                startPage, endPage, hasPrev, hasNext);
    }
}
