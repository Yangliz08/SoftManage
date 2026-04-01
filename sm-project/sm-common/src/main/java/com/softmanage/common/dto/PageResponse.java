package com.softmanage.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;
    private int pages;

    public static <T> PageResponse<T> of(List<T> records, long total, int page, int size) {
        PageResponse<T> response = new PageResponse<>();
        response.setRecords(records);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        response.setPages((int) Math.ceil((double) total / size));
        return response;
    }
}

