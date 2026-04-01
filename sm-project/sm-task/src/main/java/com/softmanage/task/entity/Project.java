package com.softmanage.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectCode;
    private String projectName;
    private String description;
    private Long managerId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualStart;
    private LocalDate actualEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}

