package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("task_blockage")
public class TaskBlockage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String previousState;
    private String blockReason;
    private String blockCategory;
    private Long blockedBy;
    private Long resolvedBy;
    private String resolution;
    private String status;
    private LocalDateTime blockedAt;
    private LocalDateTime resolvedAt;
    private BigDecimal durationHours;
}

