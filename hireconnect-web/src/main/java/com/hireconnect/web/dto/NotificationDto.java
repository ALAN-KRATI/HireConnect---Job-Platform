package com.hireconnect.web.dto;

import com.hireconnect.web.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long notificationId;
    private UUID userId;

    private String title;
    private String message;

    private NotificationType type;

    private boolean read;

    private String redirectUrl;

    private LocalDateTime createdAt;
}