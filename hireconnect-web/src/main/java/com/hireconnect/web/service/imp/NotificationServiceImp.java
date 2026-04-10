package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.NotificationDto;
import com.hireconnect.web.service.NotificationService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class NotificationServiceImp implements NotificationService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://notification-service/notifications";

    public NotificationServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<NotificationDto> getNotifications(Long userId) {

        ResponseEntity<List<NotificationDto>> response = restTemplate.exchange(
                BASE_URL + "/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<NotificationDto>>() {},
                userId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public void markAsRead(Long notificationId) {

        restTemplate.postForObject(
                BASE_URL + "/{notificationId}/read",
                null,
                Void.class,
                notificationId
        );
    }

    @Override
    public void markAllAsRead(Long userId) {

        restTemplate.postForObject(
                BASE_URL + "/user/{userId}/read-all",
                null,
                Void.class,
                userId
        );
    }
}