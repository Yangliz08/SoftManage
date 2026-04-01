package com.softmanage.audit.listener;

import com.softmanage.audit.service.AuditService;
import com.softmanage.common.constant.MQConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditService auditService;

    @RabbitListener(queues = MQConstant.AUDIT_QUEUE)
    public void handleAuditEvent(Map<String, Object> event) {
        try {
            log.info("审计服务收到事件: type={}", event.get("type"));
            auditService.recordAudit(event);
        } catch (Exception e) {
            log.error("记录审计日志失败: {}", e.getMessage(), e);
        }
    }
}

