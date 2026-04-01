package com.softmanage.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("progress_snapshot")
public class ProgressSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private LocalDate snapshotDate;
    private String snapshotType;
    private Integer totalTasks;
    private Integer planningCount;
    private Integer assignedCount;
    private Integer devCount;
    private Integer reviewCount;
    private Integer testCount;
    private Integer releaseCount;
    private Integer releasedCount;
    private Integer completedCount;
    private Integer blockedCount;
    private BigDecimal completionRate;
    private BigDecimal onTrackRate;
    private LocalDateTime createdAt;
}

