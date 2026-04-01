package com.softmanage.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String notifyType;
    private String bizType;
    private Long bizId;
    private String level;
    private Integer isRead;
    private LocalDateTime readTime;
    private Long senderId;
    private LocalDateTime createdAt;
}

