package com.hireconnect.subscription.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/email")
    void sendEmail(
        @RequestParam String email,
        @RequestParam String subject,
        @RequestParam String body
    );
}