package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.service.JobService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class JobServiceImp implements JobService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://job-service/jobs";

    public JobServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<JobDto> getAllJobs() {

        ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<JobDto>>() {}
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public JobDto getJobById(Long jobId) {

        return restTemplate.getForObject(
                BASE_URL + "/{jobId}",
                JobDto.class,
                jobId
        );
    }

    @Override
    public List<JobDto> getJobsByRecruiter(Long recruiterId) {

        ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                BASE_URL + "/recruiter/{recruiterId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<JobDto>>() {},
                recruiterId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public JobDto createJob(JobDto dto) {

        return restTemplate.postForObject(
                BASE_URL,
                dto,
                JobDto.class
        );
    }

    @Override
    public JobDto updateJob(Long jobId, JobDto dto) {

        restTemplate.put(
                BASE_URL + "/{jobId}",
                dto,
                jobId
        );

        return getJobById(jobId);
    }

    @Override
    public void deleteJob(Long jobId) {

        restTemplate.delete(
                BASE_URL + "/{jobId}",
                jobId
        );
    }

    @Override
    public void pauseJob(Long jobId) {

        restTemplate.postForObject(
                BASE_URL + "/{jobId}/pause",
                null,
                Void.class,
                jobId
        );
    }

    @Override
    public void closeJob(Long jobId) {

        restTemplate.postForObject(
                BASE_URL + "/{jobId}/close",
                null,
                Void.class,
                jobId
        );
    }

    @Override
    public List<JobDto> searchJobs(String keyword, String location) {

        ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                BASE_URL + "/search?keyword={keyword}&location={location}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<JobDto>>() {},
                keyword,
                location
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public List<JobDto> getSavedJobs(Long candidateId) {

        ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                BASE_URL + "/saved/{candidateId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<JobDto>>() {},
                candidateId
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }
}