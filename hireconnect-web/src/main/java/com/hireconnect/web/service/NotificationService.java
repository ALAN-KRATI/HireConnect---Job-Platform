package com.hireconnect.web.service;

import com.hireconnect.web.dto.NotificationDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getNotifications(Long userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(Long userId);
}