package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.AnalyticsDto;
import com.hireconnect.web.service.AnalyticsService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalyticsServiceImp implements AnalyticsService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://analytics-service/analytics";

    public AnalyticsServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AnalyticsDto getRecruiterAnalytics(Long recruiterId) {

        return restTemplate.getForObject(
                BASE_URL + "/recruiter/{recruiterId}",
                AnalyticsDto.class,
                recruiterId
        );
    }

    @Override
    public AnalyticsDto getPlatformAnalytics() {

        return restTemplate.getForObject(
                BASE_URL + "/platform",
                AnalyticsDto.class
        );
    }

    @Override
    public byte[] exportPlatformReport() {

        ResponseEntity<byte[]> response = restTemplate.exchange(
                BASE_URL + "/report/export",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<byte[]>() {
                }
        );

        return response.getBody();
    }
}