package com.softmanage.workflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.softmanage.common.constant.MQConstant;
import com.softmanage.common.exception.BusinessException;
import com.softmanage.common.result.ResultCode;
import com.softmanage.workflow.dto.BlockRequest;
import com.softmanage.workflow.dto.TransitionRequest;
import com.softmanage.workflow.dto.UnblockRequest;
import com.softmanage.workflow.entity.*;
import com.softmanage.workflow.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流引擎 - 核心服务
 * 负责状态转移、异常处理、阻塞恢复、闭环验证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngine {

    private final WorkflowDefinitionMapper workflowDefinitionMapper;
    private final StateTransitionRuleMapper transitionRuleMapper;
    private final TaskStateHistoryMapper stateHistoryMapper;
    private final TaskBlockageMapper blockageMapper;
    private final TaskExceptionMapper exceptionMapper;
    private final ClosureChainMapper closureChainMapper;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 执行状态转移 ⭐ 核心方法
     */
    @Transactional
    public Map<String, Object> transitionTask(TransitionRequest request, Long operatorId, String operatorRole) {
        // 1. 获取默认工作流定义
        WorkflowDefinition workflow = getDefaultWorkflow();

        // 2. 查找转移规则
        StateTransitionRule rule = transitionRuleMapper.findByAction(
                workflow.getId(), request.getActionCode(), request.getActionCode());

        // 如果没有通过 actionCode 直接找到，尝试通过 fromState + actionCode 组合
        if (rule == null) {
            List<StateTransitionRule> rules = transitionRuleMapper.findByWorkflowId(workflow.getId());
            rule = rules.stream()
                    .filter(r -> r.getActionCode().equals(request.getActionCode()))
                    .findFirst()
                    .orElse(null);
        }

        if (rule == null) {
            throw new BusinessException(ResultCode.WORKFLOW_TRANSITION_INVALID);
        }

        // 3. 校验角色权限
        if (!rule.getRequiredRole().equals(operatorRole) && !"ADMIN".equals(operatorRole)) {
            throw new BusinessException(ResultCode.WORKFLOW_PERMISSION_DENIED);
        }

        // 4. 计算在前状态的停留时间
        TaskStateHistory lastHistory = stateHistoryMapper.findLatestByTaskId(request.getTaskId());
        Integer durationMinutes = null;
        if (lastHistory != null) {
            durationMinutes = (int) Duration.between(lastHistory.getCreatedAt(), LocalDateTime.now()).toMinutes();
        }

        // 5. 记录状态转移历史
        TaskStateHistory history = new TaskStateHistory();
        history.setTaskId(request.getTaskId());
        history.setFromState(rule.getFromState());
        history.setToState(rule.getToState());
        history.setActionCode(request.getActionCode());
        history.setOperatorId(operatorId);
        history.setOperatorRole(operatorRole);
        history.setRemark(request.getRemark());
        history.setDurationMinutes(durationMinutes);
        history.setCreatedAt(LocalDateTime.now());
        stateHistoryMapper.insert(history);

        // 6. 如果是异常流，记录异常
        if (rule.getIsException() != null && rule.getIsException() == 1 && !"BLOCK_TASK".equals(request.getActionCode())) {
            handleException(request, rule, operatorId);
        }

        // 7. 发送状态变更事件到 MQ
        Map<String, Object> event = new HashMap<>();
        event.put("type", "STATE_CHANGED");
        event.put("taskId", request.getTaskId());
        event.put("fromState", rule.getFromState());
        event.put("toState", rule.getToState());
        event.put("actionCode", request.getActionCode());
        event.put("actionName", rule.getActionName());
        event.put("operatorId", operatorId);
        event.put("operatorRole", operatorRole);
        event.put("isException", rule.getIsException());
        event.put("remark", request.getRemark());
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.WORKFLOW_EXCHANGE, MQConstant.STATE_CHANGED_KEY, event);

        // 8. 发送通知事件
        rabbitTemplate.convertAndSend(MQConstant.NOTIFICATION_EXCHANGE, getNotificationKey(request.getActionCode()), event);

        // 9. 发送审计事件
        rabbitTemplate.convertAndSend(MQConstant.AUDIT_EXCHANGE, "workflow.transition", event);

        // 10. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", request.getTaskId());
        result.put("fromState", rule.getFromState());
        result.put("toState", rule.getToState());
        result.put("actionName", rule.getActionName());
        result.put("success", true);
        return result;
    }

    /**
     * 标记任务阻塞 ⭐ 保存 previous_state
     */
    @Transactional
    public TaskBlockage blockTask(BlockRequest request, Long operatorId) {
        TaskBlockage blockage = new TaskBlockage();
        blockage.setTaskId(request.getTaskId());
        blockage.setPreviousState(request.getCurrentState()); // ⭐ 保存阻塞前状态
        blockage.setBlockReason(request.getReason());
        blockage.setBlockCategory(request.getCategory());
        blockage.setBlockedBy(operatorId);
        blockage.setStatus("BLOCKED");
        blockage.setBlockedAt(LocalDateTime.now());
        blockageMapper.insert(blockage);

        // 记录历史
        TaskStateHistory history = new TaskStateHistory();
        history.setTaskId(request.getTaskId());
        history.setFromState(request.getCurrentState());
        history.setToState("BLOCKED");
        history.setActionCode("BLOCK_TASK");
        history.setOperatorId(operatorId);
        history.setOperatorRole(""); // 由调用方设置
        history.setRemark("阻塞原因: " + request.getReason() + " | 分类: " + request.getCategory());
        history.setCreatedAt(LocalDateTime.now());
        stateHistoryMapper.insert(history);

        // 发送事件
        Map<String, Object> event = new HashMap<>();
        event.put("type", "TASK_BLOCKED");
        event.put("taskId", request.getTaskId());
        event.put("previousState", request.getCurrentState());
        event.put("reason", request.getReason());
        event.put("category", request.getCategory());
        event.put("operatorId", operatorId);
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.NOTIFICATION_EXCHANGE, MQConstant.TASK_BLOCKED_KEY, event);
        rabbitTemplate.convertAndSend(MQConstant.AUDIT_EXCHANGE, "workflow.blockage", event);

        log.info("任务已阻塞: taskId={}, previousState={}, reason={}", request.getTaskId(), request.getCurrentState(), request.getReason());
        return blockage;
    }

    /**
     * 解除阻塞 ⭐ 使用 previous_state 精确恢复
     */
    @Transactional
    public Map<String, Object> unblockTask(UnblockRequest request, Long operatorId) {
        TaskBlockage blockage = blockageMapper.findActiveBlockage(request.getTaskId());
        if (blockage == null) {
            throw new BusinessException(ResultCode.WORKFLOW_BLOCKAGE_NOT_FOUND);
        }

        // ⭐ 使用 previous_state 精确恢复
        String recoveredState = blockage.getPreviousState();

        // 更新阻塞记录
        blockage.setResolvedBy(operatorId);
        blockage.setResolution(request.getResolution());
        blockage.setStatus("RESOLVED");
        blockage.setResolvedAt(LocalDateTime.now());
        Duration duration = Duration.between(blockage.getBlockedAt(), LocalDateTime.now());
        blockage.setDurationHours(BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP));
        blockageMapper.updateById(blockage);

        // 记录历史
        TaskStateHistory history = new TaskStateHistory();
        history.setTaskId(request.getTaskId());
        history.setFromState("BLOCKED");
        history.setToState(recoveredState); // ⭐ 恢复到之前的状态
        history.setActionCode("UNBLOCK_TASK");
        history.setOperatorId(operatorId);
        history.setOperatorRole("");
        history.setRemark("解除阻塞，恢复到: " + recoveredState + " | 方案: " + request.getResolution());
        history.setCreatedAt(LocalDateTime.now());
        stateHistoryMapper.insert(history);

        // 发送事件
        Map<String, Object> event = new HashMap<>();
        event.put("type", "TASK_UNBLOCKED");
        event.put("taskId", request.getTaskId());
        event.put("recoveredState", recoveredState);
        event.put("resolution", request.getResolution());
        event.put("operatorId", operatorId);
        event.put("timestamp", LocalDateTime.now().toString());
        rabbitTemplate.convertAndSend(MQConstant.NOTIFICATION_EXCHANGE, MQConstant.TASK_UNBLOCKED_KEY, event);

        log.info("任务已解除阻塞: taskId={}, recoveredState={}", request.getTaskId(), recoveredState);

        Map<String, Object> result = new HashMap<>();
        result.put("taskId", request.getTaskId());
        result.put("recoveredState", recoveredState);
        result.put("resolution", request.getResolution());
        return result;
    }

    /**
     * 获取任务状态历史
     */
    public List<TaskStateHistory> getTaskHistory(Long taskId) {
        return stateHistoryMapper.findByTaskId(taskId);
    }

    /**
     * 获取可执行的状态转移
     */
    public List<StateTransitionRule> getValidTransitions(String currentState, String role) {
        WorkflowDefinition workflow = getDefaultWorkflow();
        List<StateTransitionRule> rules = transitionRuleMapper.findByFromState(workflow.getId(), currentState);
        return rules.stream()
                .filter(r -> r.getRequiredRole().equals(role) || "ADMIN".equals(role))
                .toList();
    }

    /**
     * 获取所有转移规则
     */
    public List<StateTransitionRule> getAllRules() {
        WorkflowDefinition workflow = getDefaultWorkflow();
        return transitionRuleMapper.findByWorkflowId(workflow.getId());
    }

    /**
     * 获取工作流定义
     */
    public WorkflowDefinition getWorkflowDefinition() {
        return getDefaultWorkflow();
    }

    /**
     * 获取闭环链
     */
    public List<ClosureChain> getClosureChains(Long taskId) {
        return closureChainMapper.selectList(
                new LambdaQueryWrapper<ClosureChain>().eq(ClosureChain::getTaskId, taskId));
    }

    // ============= 私有方法 =============

    private WorkflowDefinition getDefaultWorkflow() {
        LambdaQueryWrapper<WorkflowDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowDefinition::getWorkflowCode, "DEFAULT_WORKFLOW")
                .eq(WorkflowDefinition::getStatus, "ACTIVE")
                .orderByDesc(WorkflowDefinition::getVersion)
                .last("LIMIT 1");
        WorkflowDefinition workflow = workflowDefinitionMapper.selectOne(wrapper);
        if (workflow == null) {
            throw new BusinessException(ResultCode.WORKFLOW_DEFINITION_NOT_FOUND);
        }
        return workflow;
    }

    private void handleException(TransitionRequest request, StateTransitionRule rule, Long operatorId) {
        String exceptionType = mapActionToExceptionType(request.getActionCode());
        if (exceptionType == null) return;

        TaskException exception = new TaskException();
        exception.setTaskId(request.getTaskId());
        exception.setExceptionType(exceptionType);
        exception.setFromState(rule.getFromState());
        exception.setToState(rule.getToState());
        exception.setReason(request.getRemark() != null ? request.getRemark() : rule.getActionName());
        exception.setOperatorId(operatorId);
        exception.setStatus("OPEN");
        exception.setCreatedAt(LocalDateTime.now());
        exceptionMapper.insert(exception);

        // 创建闭环链
        ClosureChain chain = new ClosureChain();
        chain.setTaskId(request.getTaskId());
        chain.setExceptionId(exception.getId());
        chain.setChainStatus("INITIATED");
        chain.setStep1FixDone(0);
        chain.setStep2ReviewPassed(0);
        chain.setStep3TestPassed(0);
        chain.setStep4Closed(0);
        chain.setCreatedAt(LocalDateTime.now());
        closureChainMapper.insert(chain);

        log.info("异常记录已创建: taskId={}, type={}, exceptionId={}", request.getTaskId(), exceptionType, exception.getId());
    }

    private String mapActionToExceptionType(String actionCode) {
        return switch (actionCode) {
            case "REJECT_REVIEW" -> "REVIEW_REJECT";
            case "FAIL_TEST" -> "TEST_FAIL";
            case "REJECT_RELEASE" -> "RELEASE_REJECT";
            default -> null;
        };
    }

    private String getNotificationKey(String actionCode) {
        return switch (actionCode) {
            case "APPROVE_REVIEW", "REJECT_REVIEW" -> MQConstant.REVIEW_RESULT_KEY;
            case "PASS_TEST", "FAIL_TEST" -> MQConstant.TEST_RESULT_KEY;
            case "APPROVE_RELEASE", "REJECT_RELEASE" -> MQConstant.RELEASE_RESULT_KEY;
            default -> MQConstant.STATE_CHANGED_KEY;
        };
    }
}

