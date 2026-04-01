package com.softmanage.notification.controller;

import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import com.softmanage.notification.entity.Notification;
import com.softmanage.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<PageResponse<Notification>> getNotifications(
            @RequestHeader(AuthConstant.USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly) {
        return Result.success(notificationService.getNotifications(userId, page, size, unreadOnly));
    }

    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return Result.success();
    }
}

