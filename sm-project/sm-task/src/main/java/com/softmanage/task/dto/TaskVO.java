package com.softmanage.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO {
    private Long id;
    private String taskCode;
    private String taskName;
    private String description;
    private Long projectId;
    private String projectName;
    private Long requirementId;
    private Long parentTaskId;
    private String taskType;
    private String currentState;
    private String previousState;
    private String priority;
    private Integer progress;
    private Long creatorId;
    private String creatorName;
    private Long assigneeId;
    private String assigneeName;
    private Long reviewerId;
    private String reviewerName;
    private Long testerId;
    private String testerName;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualStart;
    private LocalDate actualEnd;
    private String codeUrl;
    private Integer isBlocked;
    private String blockReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

