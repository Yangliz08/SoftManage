package com.softmanage.workflow.dto;

import lombok.Data;

@Data
public class UnblockRequest {
    private Long taskId;
    private String resolution;
}

