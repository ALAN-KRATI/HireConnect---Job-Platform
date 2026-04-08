package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.AnalyticsDto;
import com.hireconnect.web.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalyticsServiceImp implements AnalyticsService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8088/analytics";

    @Override
    public AnalyticsDto getRecruiterAnalytics(Long recruiterId) {
        return restTemplate.getForObject(BASE_URL + "/recruiter/" + recruiterId, AnalyticsDto.class);
    }

    @Override
    public AnalyticsDto getPlatformAnalytics() {
        return restTemplate.getForObject(BASE_URL + "/platform", AnalyticsDto.class);
    }

    @Override
    public byte[] exportPlatformReport() {
        return restTemplate.getForObject(BASE_URL + "/export", byte[].class);
    }
}