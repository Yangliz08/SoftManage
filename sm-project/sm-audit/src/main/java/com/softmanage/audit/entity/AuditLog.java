package com.softmanage.audit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String traceId;
    private String serviceName;
    private String module;
    private String action;
    private String method;
    private String requestUrl;
    private String requestParams;
    private Integer responseCode;
    private String responseData;
    private Long userId;
    private String username;
    private String userRole;
    private String ipAddress;
    private String userAgent;
    private Long executionTime;
    private Integer status;
    private String errorMessage;
    private LocalDateTime createdAt;
}

