package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_state_history")
public class TaskStateHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String fromState;
    private String toState;
    private String actionCode;
    private Long operatorId;
    private String operatorRole;
    private String remark;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
}

