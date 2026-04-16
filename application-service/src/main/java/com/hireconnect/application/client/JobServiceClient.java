package com.hireconnect.application.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JobServiceClient {

    private final RestTemplate restTemplate;

    @Value("${JOB_SERVICE_URL:http://job-service:8083}")
    private String jobServiceUrl;

    public JobServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UUID getPostedBy(Long jobId) {
        try {
            String url = jobServiceUrl + "/jobs/" + jobId;
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Object postedBy = response.getBody() != null ? response.getBody().get("postedBy") : null;
            if (postedBy == null) return null;
            return UUID.fromString(postedBy.toString());
        } catch (Exception e) {
            log.warn("Could not resolve recruiter for job {}: {}", jobId, e.getMessage());
            return null;
        }
    }
}
