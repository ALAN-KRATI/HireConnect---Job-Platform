package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.AnalyticsDto;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.util.UrlConstants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class AnalyticsServiceImp implements AnalyticsService {

    private final RestTemplate restTemplate;

    public AnalyticsServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AnalyticsDto getRecruiterAnalytics(UUID recruiterId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.ANALYTICS_SERVICE + "/recruiter/{recruiterId}",
                    AnalyticsDto.class,
                    recruiterId
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load recruiter analytics.");
        }
    }

    @Override
    public AnalyticsDto getPlatformAnalytics() {
        try {
            return restTemplate.getForObject(
                    UrlConstants.ANALYTICS_SERVICE + "/platform",
                    AnalyticsDto.class
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load platform analytics.");
        }
    }

    @Override
    public byte[] exportPlatformReport() {
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    UrlConstants.ANALYTICS_SERVICE + "/report/export",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to export analytics report.");
        }
    }
}