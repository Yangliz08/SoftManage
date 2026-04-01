package com.softmanage.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransitionRequest {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    @NotBlank(message = "动作编码不能为空")
    private String actionCode;
    private String remark;
    private String codeUrl;
}

