package com.softmanage.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("defect")
public class Defect {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String defectCode;
    private String defectName;
    private String description;
    private Long taskId;
    private Long projectId;
    private String severity;
    private String status;
    private Long reporterId;
    private Long assigneeId;
    private String stepsToReproduce;
    private String expectedResult;
    private String actualResult;
    private String environment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}

