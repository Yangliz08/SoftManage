package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("workflow_definition")
public class WorkflowDefinition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String workflowCode;
    private String workflowName;
    private String description;
    private String definitionJson;
    private Integer version;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}

