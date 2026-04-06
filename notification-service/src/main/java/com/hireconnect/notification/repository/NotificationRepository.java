package com.hireconnect.notification.repository;

import com.hireconnect.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserId(Integer userId);

    List<Notification> findByUserIdAndIsRead(Integer userId, boolean isRead);

    int countByUserIdAndIsRead(Integer userId, boolean isRead);

    void deleteByNotificationId(Integer notificationId);
}