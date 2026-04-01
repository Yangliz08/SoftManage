package com.softmanage.common.dto;

import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {
    private int page = 1;
    private int size = 10;
    private String sortBy;
    private String sortOrder = "desc";
}

