package com.softmanage.notification.listener;

import com.softmanage.common.constant.MQConstant;
import com.softmanage.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 监听工作流事件，发送通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = MQConstant.NOTIFICATION_QUEUE)
    public void handleEvent(Map<String, Object> event) {
        try {
            String type = (String) event.get("type");
            log.info("通知服务收到事件: type={}", type);

            switch (type) {
                case "TASK_ASSIGNED" -> handleTaskAssigned(event);
                case "STATE_CHANGED" -> handleStateChanged(event);
                case "TASK_BLOCKED" -> handleTaskBlocked(event);
                case "TASK_UNBLOCKED" -> handleTaskUnblocked(event);
                default -> log.debug("忽略事件类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理通知事件失败: {}", e.getMessage(), e);
        }
    }

    private void handleTaskAssigned(Map<String, Object> event) {
        Long assigneeId = toLong(event.get("assigneeId"));
        Long operatorId = toLong(event.get("operatorId"));
        Long taskId = toLong(event.get("taskId"));
        String taskName = (String) event.get("taskName");
        String taskCode = (String) event.get("taskCode");

        if (assigneeId != null) {
            Map<String, String> vars = new HashMap<>();
            vars.put("taskName", taskName);
            vars.put("taskCode", taskCode);
            vars.put("pmName", "项目经理");
            vars.put("priority", "MEDIUM");
            notificationService.sendNotification("TASK_ASSIGNED", assigneeId, operatorId, vars, taskId);
        }
    }

    private void handleStateChanged(Map<String, Object> event) {
        String actionCode = (String) event.get("actionCode");
        Long taskId = toLong(event.get("taskId"));
        Long operatorId = toLong(event.get("operatorId"));
        String taskName = (String) event.getOrDefault("taskName", "");
        String taskCode = (String) event.getOrDefault("taskCode", "");
        String remark = (String) event.getOrDefault("remark", "");

        Map<String, String> vars = new HashMap<>();
        vars.put("taskName", taskName);
        vars.put("taskCode", taskCode);
        vars.put("reason", remark);

        String templateCode = mapActionToTemplate(actionCode);
        if (templateCode != null && operatorId != null) {
            // 通知任务相关人员
            notificationService.sendNotification(templateCode, operatorId, null, vars, taskId);
        }
    }

    private void handleTaskBlocked(Map<String, Object> event) {
        Long taskId = toLong(event.get("taskId"));
        Long operatorId = toLong(event.get("operatorId"));
        String reason = (String) event.getOrDefault("reason", "");
        String category = (String) event.getOrDefault("category", "");

        Map<String, String> vars = new HashMap<>();
        vars.put("taskName", "");
        vars.put("taskCode", "");
        vars.put("reason", reason);
        vars.put("category", category);

        if (operatorId != null) {
            notificationService.sendNotification("TASK_BLOCKED", operatorId, null, vars, taskId);
        }
    }

    private void handleTaskUnblocked(Map<String, Object> event) {
        Long taskId = toLong(event.get("taskId"));
        Long operatorId = toLong(event.get("operatorId"));
        String recoveredState = (String) event.getOrDefault("recoveredState", "");

        Map<String, String> vars = new HashMap<>();
        vars.put("taskName", "");
        vars.put("taskCode", "");
        vars.put("recoveredState", recoveredState);

        if (operatorId != null) {
            notificationService.sendNotification("TASK_UNBLOCKED", operatorId, null, vars, taskId);
        }
    }

    private String mapActionToTemplate(String actionCode) {
        return switch (actionCode) {
            case "APPROVE_REVIEW" -> "REVIEW_APPROVED";
            case "REJECT_REVIEW" -> "REVIEW_REJECTED";
            case "PASS_TEST" -> "TEST_PASSED";
            case "FAIL_TEST" -> "TEST_FAILED";
            case "APPROVE_RELEASE" -> "RELEASE_APPROVED";
            case "REJECT_RELEASE" -> "RELEASE_REJECTED";
            default -> null;
        };
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        try { return Long.parseLong(obj.toString()); } catch (Exception e) { return null; }
    }
}

