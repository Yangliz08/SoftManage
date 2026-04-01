package com.softmanage.audit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("data_change_log")
public class DataChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long auditLogId;
    private String tableName;
    private Long recordId;
    private String changeType;
    private String oldValue;
    private String newValue;
    private String changedFields;
    private Long userId;
    private LocalDateTime createdAt;
}

