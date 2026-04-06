package com.hireconnect.notification.controller;

import com.hireconnect.notification.dto.EmailRequest;
import com.hireconnect.notification.dto.NotificationRequest;
import com.hireconnect.notification.entity.Notification;
import com.hireconnect.notification.service.NotificationService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationResource {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> sendNotification(
            @Valid @RequestBody NotificationRequest dto) {

        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setType(dto.getType());
        notification.setMessage(dto.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationService.sendNotification(notification);

        return ResponseEntity.ok("Notification sent successfully");
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest dto) {

        notificationService.sendEmailAlert(
                dto.getEmail(),
                dto.getSubject(),
                dto.getBody()
        );

        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getByUser(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllRead(@PathVariable Integer userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Integer notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }
}