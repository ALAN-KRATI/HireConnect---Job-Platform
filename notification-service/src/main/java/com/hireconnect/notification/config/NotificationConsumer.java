package com.hireconnect.notification.config;

import com.hireconnect.notification.entity.Notification;
import com.hireconnect.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consume(Notification notification) {
        notificationService.sendNotification(notification);
    }
}