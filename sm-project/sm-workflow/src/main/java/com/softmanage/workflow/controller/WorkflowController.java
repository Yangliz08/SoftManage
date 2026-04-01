package com.softmanage.workflow.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.result.Result;
import com.softmanage.workflow.dto.BlockRequest;
import com.softmanage.workflow.dto.TransitionRequest;
import com.softmanage.workflow.dto.UnblockRequest;
import com.softmanage.workflow.entity.*;
import com.softmanage.workflow.service.WorkflowEngine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowEngine workflowEngine;

    /**
     * 执行状态转移
     */
    @PostMapping("/tasks/{taskId}/transition")
    public Result<Map<String, Object>> transitionTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TransitionRequest request,
            @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId,
            @RequestHeader(AuthConstant.ROLE_HEADER) String role) {
        request.setTaskId(taskId);
        return Result.success(workflowEngine.transitionTask(request, userId, role));
    }

    /**
     * 标记阻塞
     */
    @PostMapping("/tasks/{taskId}/block")
    public Result<TaskBlockage> blockTask(
            @PathVariable Long taskId,
            @Valid @RequestBody BlockRequest request,
            @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        request.setTaskId(taskId);
        return Result.success(workflowEngine.blockTask(request, userId));
    }

    /**
     * 解除阻塞
     */
    @PostMapping("/tasks/{taskId}/unblock")
    public Result<Map<String, Object>> unblockTask(
            @PathVariable Long taskId,
            @RequestBody UnblockRequest request,
            @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        request.setTaskId(taskId);
        return Result.success(workflowEngine.unblockTask(request, userId));
    }

    /**
     * 获取任务状态历史
     */
    @GetMapping("/tasks/{taskId}/history")
    public Result<List<TaskStateHistory>> getTaskHistory(@PathVariable Long taskId) {
        return Result.success(workflowEngine.getTaskHistory(taskId));
    }

    /**
     * 获取可执行的状态转移
     */
    @GetMapping("/transitions")
    public Result<List<StateTransitionRule>> getValidTransitions(
            @RequestParam String currentState,
            @RequestHeader(AuthConstant.ROLE_HEADER) String role) {
        return Result.success(workflowEngine.getValidTransitions(currentState, role));
    }

    /**
     * 获取所有转移规则
     */
    @GetMapping("/rules")
    public Result<List<StateTransitionRule>> getAllRules() {
        return Result.success(workflowEngine.getAllRules());
    }

    /**
     * 获取工作流定义
     */
    @GetMapping("/definition")
    public Result<WorkflowDefinition> getDefinition() {
        return Result.success(workflowEngine.getWorkflowDefinition());
    }

    /**
     * 获取闭环链
     */
    @GetMapping("/tasks/{taskId}/closure-chains")
    public Result<List<ClosureChain>> getClosureChains(@PathVariable Long taskId) {
        return Result.success(workflowEngine.getClosureChains(taskId));
    }
}

