package com.softmanage.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.audit.entity.AuditLog;
import com.softmanage.audit.mapper.AuditLogMapper;
import com.softmanage.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogMapper auditLogMapper;

    /**
     * 记录审计日志
     */
    public void recordAudit(Map<String, Object> event) {
        AuditLog auditLog = new AuditLog();
        auditLog.setServiceName(getStr(event, "serviceName", "unknown"));
        auditLog.setModule(getStr(event, "module", "unknown"));
        auditLog.setAction(getStr(event, "type", ""));
        auditLog.setMethod("EVENT");
        auditLog.setRequestUrl("");
        auditLog.setRequestParams(event.toString());
        auditLog.setUserId(toLong(event.get("operatorId")));
        auditLog.setUserRole(getStr(event, "operatorRole", ""));
        auditLog.setStatus(1);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(auditLog);
    }

    /**
     * 查询审计日志（分页）
     */
    public PageResponse<AuditLog> getAuditLogs(String serviceName, String action, Long userId,
                                                 int page, int size) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (serviceName != null) wrapper.eq(AuditLog::getServiceName, serviceName);
        if (action != null) wrapper.like(AuditLog::getAction, action);
        if (userId != null) wrapper.eq(AuditLog::getUserId, userId);
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        Page<AuditLog> result = auditLogMapper.selectPage(pageParam, wrapper);
        return PageResponse.of(result.getRecords(), result.getTotal(), page, size);
    }

    private String getStr(Map<String, Object> map, String key, String defaultVal) {
        Object val = map.get(key);
        return val != null ? val.toString() : defaultVal;
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        try { return Long.parseLong(obj.toString()); } catch (Exception e) { return null; }
    }
}

