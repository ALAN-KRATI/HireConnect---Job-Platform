package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ApplicationDto;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.ApplicationService;
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

@Service
public class ApplicationServiceImp implements ApplicationService {

    private final RestTemplate restTemplate;

    public ApplicationServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void applyForJob(Long candidateId, Long jobId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.APPLICATION_SERVICE + "/apply?candidateId={candidateId}&jobId={jobId}",
                    null,
                    Void.class,
                    candidateId,
                    jobId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Job or candidate not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to apply for the job right now.");
        }
    }

    @Override
    public List<ApplicationDto> getApplicationsByCandidate(Long candidateId) {
        try {
            ResponseEntity<List<ApplicationDto>> response = restTemplate.exchange(
                    UrlConstants.APPLICATION_SERVICE + "/candidate/{candidateId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    candidateId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load your applications.");
        }
    }

    @Override
    public List<ApplicationDto> getApplicationsForRecruiter(Long recruiterId) {
        try {
            ResponseEntity<List<ApplicationDto>> response = restTemplate.exchange(
                    UrlConstants.APPLICATION_SERVICE + "/recruiter/{recruiterId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    recruiterId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load recruiter applications.");
        }
    }

    @Override
    public void shortlistCandidate(Long applicationId) {
        updateApplicationStatus(applicationId, "shortlist", "shortlist");
    }

    @Override
    public void rejectCandidate(Long applicationId) {
        updateApplicationStatus(applicationId, "reject", "reject");
    }

    @Override
    public void offerCandidate(Long applicationId) {
        updateApplicationStatus(applicationId, "offer", "offer");
    }

    @Override
    public ApplicationDto getApplicationById(Long applicationId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.APPLICATION_SERVICE + "/{applicationId}",
                    ApplicationDto.class,
                    applicationId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Application not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load application details.");
        }
    }

    private void updateApplicationStatus(Long applicationId, String action, String actionName) {
        try {
            restTemplate.postForObject(
                    UrlConstants.APPLICATION_SERVICE + "/{applicationId}/" + action,
                    null,
                    Void.class,
                    applicationId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Application not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException(
                    "Unable to " + actionName + " the candidate right now."
            );
        }
    }
}