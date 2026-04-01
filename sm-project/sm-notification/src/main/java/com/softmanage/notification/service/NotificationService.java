package com.softmanage.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.notification.entity.Notification;
import com.softmanage.notification.entity.NotificationTemplate;
import com.softmanage.notification.mapper.NotificationMapper;
import com.softmanage.notification.mapper.NotificationTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationTemplateMapper templateMapper;

    /**
     * 发送通知（使用模板）
     */
    public void sendNotification(String templateCode, Long userId, Long senderId, Map<String, String> variables, Long bizId) {
        NotificationTemplate template = templateMapper.findByCode(templateCode);
        if (template == null) {
            log.warn("通知模板不存在: {}", templateCode);
            return;
        }

        String title = replaceVariables(template.getTitleTemplate(), variables);
        String content = replaceVariables(template.getContentTemplate(), variables);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotifyType(template.getNotifyType());
        notification.setBizType("TASK");
        notification.setBizId(bizId);
        notification.setLevel("INFO");
        notification.setIsRead(0);
        notification.setSenderId(senderId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);

        log.info("通知已发送: userId={}, template={}, title={}", userId, templateCode, title);
    }

    /**
     * 直接发送通知
     */
    public void sendDirectNotification(Long userId, String title, String content, String notifyType, String level, Long bizId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotifyType(notifyType);
        notification.setBizType("TASK");
        notification.setBizId(bizId);
        notification.setLevel(level != null ? level : "INFO");
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 获取通知列表
     */
    public PageResponse<Notification> getNotifications(Long userId, int page, int size, Boolean unreadOnly) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (Boolean.TRUE.equals(unreadOnly)) {
            wrapper.eq(Notification::getIsRead, 0);
        }
        wrapper.orderByDesc(Notification::getCreatedAt);
        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);
        return PageResponse.of(result.getRecords(), result.getTotal(), page, size);
    }

    /**
     * 标记已读
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    /**
     * 标记全部已读
     */
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    /**
     * 获取未读数量
     */
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteById(notificationId);
    }

    private String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
            }
        }
        return result;
    }
}

