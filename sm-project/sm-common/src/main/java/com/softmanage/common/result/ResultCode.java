package com.softmanage.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未认证，请先登录"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    TOKEN_INVALID(1003, "Token无效"),
    ACCESS_DENIED(1004, "权限不足"),
    LOGIN_FAILED(1005, "用户名或密码错误"),
    ACCOUNT_DISABLED(1006, "账户已被禁用"),

    // 参数校验 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "缺少必要参数"),
    PARAM_TYPE_ERROR(2003, "参数类型错误"),

    // 业务异常 3xxx
    DATA_NOT_FOUND(3001, "数据不存在"),
    DATA_ALREADY_EXISTS(3002, "数据已存在"),
    OPERATION_NOT_ALLOWED(3003, "操作不允许"),

    // 任务相关 4xxx
    TASK_NOT_FOUND(4001, "任务不存在"),
    TASK_STATE_ERROR(4002, "任务状态不允许此操作"),
    TASK_ALREADY_ASSIGNED(4003, "任务已分配"),
    TASK_BLOCKED(4004, "任务已被阻塞"),

    // 工作流相关 5xxx
    WORKFLOW_TRANSITION_INVALID(5001, "状态转移不合法"),
    WORKFLOW_PERMISSION_DENIED(5002, "无权执行此状态转移"),
    WORKFLOW_DEFINITION_NOT_FOUND(5003, "流程定义不存在"),
    WORKFLOW_BLOCKAGE_NOT_FOUND(5004, "阻塞记录不存在");

    private final int code;
    private final String message;
}

