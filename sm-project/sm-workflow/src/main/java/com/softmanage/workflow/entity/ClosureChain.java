package com.softmanage.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("closure_chain")
public class ClosureChain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long exceptionId;
    private Long fixTaskId;
    private String chainStatus;
    private Integer step1FixDone;
    private LocalDateTime step1Time;
    private Integer step2ReviewPassed;
    private LocalDateTime step2Time;
    private Integer step3TestPassed;
    private LocalDateTime step3Time;
    private Integer step4Closed;
    private LocalDateTime step4Time;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

