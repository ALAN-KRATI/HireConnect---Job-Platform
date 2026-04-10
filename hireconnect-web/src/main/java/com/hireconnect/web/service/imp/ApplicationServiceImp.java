package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ApplicationDto;
import com.hireconnect.web.service.ApplicationService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class ApplicationServiceImp implements ApplicationService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://application-service/applications";

    public ApplicationServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void applyForJob(Long candidateId, Long jobId) {

        restTemplate.postForObject(
                BASE_URL + "/apply?candidateId={candidateId}&jobId={jobId}",
                null,
                Void.class,
                candidateId,
                jobId
        );
    }

    @Override
    public List<ApplicationDto> getApplicationsByCandidate(Long candidateId) {

        ResponseEntity<List<ApplicationDto>> response = restTemplate.exchange(
                BASE_URL + "/candidate/{candidateId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ApplicationDto>>() {},
                candidateId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public List<ApplicationDto> getApplicationsForRecruiter(Long recruiterId) {

        ResponseEntity<List<ApplicationDto>> response = restTemplate.exchange(
                BASE_URL + "/recruiter/{recruiterId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ApplicationDto>>() {},
                recruiterId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public void shortlistCandidate(Long applicationId) {

        restTemplate.postForObject(
                BASE_URL + "/{applicationId}/shortlist",
                null,
                Void.class,
                applicationId
        );
    }

    @Override
    public void rejectCandidate(Long applicationId) {

        restTemplate.postForObject(
                BASE_URL + "/{applicationId}/reject",
                null,
                Void.class,
                applicationId
        );
    }

    @Override
    public void offerCandidate(Long applicationId) {

        restTemplate.postForObject(
                BASE_URL + "/{applicationId}/offer",
                null,
                Void.class,
                applicationId
        );
    }

    @Override
    public ApplicationDto getApplicationById(Long applicationId) {

        return restTemplate.getForObject(
                BASE_URL + "/{applicationId}",
                ApplicationDto.class,
                applicationId
        );
    }
}