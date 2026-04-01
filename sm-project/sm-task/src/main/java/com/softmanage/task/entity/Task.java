package com.softmanage.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private String taskName;
    private String description;
    private Long projectId;
    private Long requirementId;
    private Long parentTaskId;
    private String taskType;
    private String currentState;
    private String previousState;
    private String priority;
    private Integer progress;
    private Long creatorId;
    private Long assigneeId;
    private Long reviewerId;
    private Long testerId;
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
    @TableLogic
    private Integer deleted;
}

