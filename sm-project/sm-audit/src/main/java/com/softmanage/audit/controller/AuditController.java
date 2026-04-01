package com.softmanage.audit.controller;

import com.softmanage.audit.entity.AuditLog;
import com.softmanage.audit.service.AuditService;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/logs")
    public Result<PageResponse<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(auditService.getAuditLogs(serviceName, action, userId, page, size));
    }
}

