package com.hireconnect.web.service;

import com.hireconnect.web.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationDto> getNotifications(UUID userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(UUID userId);
}