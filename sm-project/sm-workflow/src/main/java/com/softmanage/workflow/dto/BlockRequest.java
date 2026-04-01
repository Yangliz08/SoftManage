package com.softmanage.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlockRequest {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    @NotBlank(message = "阻塞原因不能为空")
    private String reason;
    @NotBlank(message = "阻塞分类不能为空")
    private String category;
    @NotBlank(message = "当前状态不能为空")
    private String currentState;
}

