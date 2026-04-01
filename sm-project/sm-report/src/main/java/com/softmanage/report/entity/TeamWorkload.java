package com.softmanage.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("team_workload")
public class TeamWorkload {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long projectId;
    private LocalDate statDate;
    private String statPeriod;
    private Integer assignedTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer blockedTasks;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private LocalDateTime createdAt;
}

