package com.softmanage.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("gantt_task")
public class GanttTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long projectId;
    private String taskName;
    private String assigneeName;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    private LocalDate actualStart;
    private LocalDate actualEnd;
    private Integer progress;
    private String dependencyIds;
    private Integer milestone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

