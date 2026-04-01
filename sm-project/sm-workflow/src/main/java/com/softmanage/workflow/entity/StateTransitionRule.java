package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("state_transition_rule")
public class StateTransitionRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workflowId;
    private String fromState;
    private String toState;
    private String actionName;
    private String actionCode;
    private String requiredRole;
    private String description;
    private Integer isException;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

