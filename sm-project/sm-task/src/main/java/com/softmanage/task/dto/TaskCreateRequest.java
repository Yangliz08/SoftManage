package com.softmanage.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaskCreateRequest {
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    private String description;
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    private Long requirementId;
    private Long parentTaskId;
    private String taskType = "NORMAL";
    private String priority = "MEDIUM";
    private Long assigneeId;
    private Long reviewerId;
    private Long testerId;
    private BigDecimal estimatedHours;
    private LocalDate startDate;
    private LocalDate endDate;
}

