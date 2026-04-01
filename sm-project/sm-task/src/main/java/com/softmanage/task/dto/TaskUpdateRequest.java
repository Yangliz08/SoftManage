package com.softmanage.task.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaskUpdateRequest {
    private String taskName;
    private String description;
    private String priority;
    private Long assigneeId;
    private Long reviewerId;
    private Long testerId;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private Integer progress;
    private LocalDate startDate;
    private LocalDate endDate;
    private String codeUrl;
}

