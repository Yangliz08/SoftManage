package com.softmanage.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification_template")
public class NotificationTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String templateCode;
    private String templateName;
    private String titleTemplate;
    private String contentTemplate;
    private String notifyType;
    private String channel;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

