package com.softmanage.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("requirement")
public class Requirement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String reqCode;
    private String reqName;
    private String description;
    private Long projectId;
    private String priority;
    private String status;
    private Long creatorId;
    private Long assigneeId;
    private BigDecimal estimatedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}

