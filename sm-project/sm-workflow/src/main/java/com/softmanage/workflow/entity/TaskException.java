package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_exception")
public class TaskException {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String exceptionType;
    private String fromState;
    private String toState;
    private String reason;
    private Long operatorId;
    private Long fixTaskId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}

