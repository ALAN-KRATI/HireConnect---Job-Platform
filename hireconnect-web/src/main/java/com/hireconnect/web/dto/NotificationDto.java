package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.hireconnect.web.enums.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private Long userId;

    private String title;
    private String message;
    private NotificationType type;

    private boolean read;

    private LocalDateTime createdAt;
}