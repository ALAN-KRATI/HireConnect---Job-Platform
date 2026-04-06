package com.hireconnect.notification.service;

import com.hireconnect.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(Notification notification);

    void markAsRead(Integer notificationId);

    void markAllRead(Integer userId);

    List<Notification> getByUser(Integer userId);

    void deleteNotification(Integer notificationId);

    void sendEmailAlert(String email, String subject, String body);

    int getUnreadCount(Integer userId);
}