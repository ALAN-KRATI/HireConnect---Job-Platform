package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.exception.BadRequestException;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.JobService;
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
public class JobServiceImp implements JobService {

    private final RestTemplate restTemplate;

    public JobServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<JobDto> getAllJobs() {
        try {
            ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                    UrlConstants.JOB_SERVICE,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load jobs.");
        }
    }

    @Override
    public JobDto getJobById(Long jobId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.JOB_SERVICE + "/{jobId}",
                    JobDto.class,
                    jobId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Job not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load job details.");
        }
    }

    @Override
    public List<JobDto> getJobsByRecruiter(UUID recruiterId) {
        try {
            ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                    UrlConstants.JOB_SERVICE + "/recruiter/{recruiterId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    recruiterId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load recruiter jobs.");
        }
    }

    @Override
    public JobDto createJob(JobDto dto) {
        validateJob(dto);

        try {
            return restTemplate.postForObject(
                    UrlConstants.JOB_SERVICE,
                    dto,
                    JobDto.class
            );
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to create job.");
        }
    }

    @Override
    public JobDto updateJob(Long jobId, JobDto dto) {
        validateJob(dto);

        try {
            restTemplate.put(
                    UrlConstants.JOB_SERVICE + "/{jobId}",
                    dto,
                    jobId
            );

            return getJobById(jobId);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Job not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to update job.");
        }
    }

    @Override
    public void deleteJob(Long jobId) {
        try {
            restTemplate.delete(
                    UrlConstants.JOB_SERVICE + "/{jobId}",
                    jobId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Job not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to delete job.");
        }
    }

    @Override
    public void pauseJob(Long jobId) {
        updateJobState(jobId, "pause", "pause");
    }

    @Override
    public void closeJob(Long jobId) {
        updateJobState(jobId, "close", "close");
    }

    @Override
    public List<JobDto> searchJobs(String keyword, String location) {
        try {
            ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                    UrlConstants.JOB_SERVICE + "/search?keyword={keyword}&location={location}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    keyword != null ? keyword : "",
                    location != null ? location : ""
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to search jobs.");
        }
    }

    @Override
    public List<JobDto> getSavedJobs(UUID candidateId) {
        try {
            ResponseEntity<List<JobDto>> response = restTemplate.exchange(
                    UrlConstants.JOB_SERVICE + "/saved/{candidateId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    candidateId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load saved jobs.");
        }
    }

    private void updateJobState(Long jobId, String action, String actionName) {
        try {
            restTemplate.postForObject(
                    UrlConstants.JOB_SERVICE + "/{jobId}/" + action,
                    null,
                    Void.class,
                    jobId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Job not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "Unable to " + actionName + " job right now."
            );
        }
    }

    private void validateJob(JobDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Job title is required.");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BadRequestException("Job description is required.");
        }

        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new BadRequestException("Company name is required.");
        }

        if (dto.getMinSalary() != null
                && dto.getMaxSalary() != null
                && dto.getMinSalary() > dto.getMaxSalary()) {
            throw new BadRequestException("Minimum salary cannot be greater than maximum salary.");
        }
    }
}