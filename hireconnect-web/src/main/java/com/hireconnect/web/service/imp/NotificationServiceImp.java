package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.NotificationDto;
import com.hireconnect.web.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationServiceImp implements NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/notifications";

    @Override
    public List<NotificationDto> getNotifications(Long userId) {
        NotificationDto[] response = restTemplate.getForObject(BASE_URL + "/" + userId, NotificationDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public void markAsRead(Long id) {
        restTemplate.postForObject(BASE_URL + "/" + id + "/read", null, Void.class);
    }

    @Override
    public void markAllAsRead(Long userId) {
        restTemplate.postForObject(
                BASE_URL + "/user/" + userId + "/read-all",
                null,
                Void.class);
    }
}