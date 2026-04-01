package com.softmanage.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exception_statistics")
public class ExceptionStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private LocalDate statDate;
    private String statPeriod;
    private Integer reviewRejectCount;
    private Integer testFailCount;
    private Integer releaseRejectCount;
    private Integer blockCount;
    private BigDecimal avgFixHours;
    private BigDecimal avgBlockHours;
    private LocalDateTime createdAt;
}

