package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.NotificationDto;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.NotificationService;
import com.hireconnect.web.util.UrlConstants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImp implements NotificationService {

    private final RestTemplate restTemplate;

    public NotificationServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<NotificationDto> getNotifications(UUID userId) {
        try {
            ResponseEntity<List<NotificationDto>> response = restTemplate.exchange(
                    UrlConstants.NOTIFICATION_SERVICE + "/{userId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    userId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load notifications.");
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.NOTIFICATION_SERVICE + "/{notificationId}/read",
                    null,
                    Void.class,
                    notificationId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Notification not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to mark notification as read.");
        }
    }

    @Override
    public void markAllAsRead(UUID userId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.NOTIFICATION_SERVICE + "/user/{userId}/read-all",
                    null,
                    Void.class,
                    userId
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to mark all notifications as read.");
        }
    }
}