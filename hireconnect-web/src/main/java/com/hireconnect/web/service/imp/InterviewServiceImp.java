package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.exception.ResourceNotFoundException;
import com.hireconnect.web.exception.ServiceUnavailableException;
import com.hireconnect.web.service.InterviewService;
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
public class InterviewServiceImp implements InterviewService {

    private final RestTemplate restTemplate;

    public InterviewServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void scheduleInterview(InterviewDto dto) {
        try {
            restTemplate.postForObject(
                    UrlConstants.INTERVIEW_SERVICE,
                    dto,
                    Void.class
            );
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new ServiceUnavailableException("Invalid interview details provided.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to schedule interview right now.");
        }
    }

    @Override
    public List<InterviewDto> getCandidateInterviews(Long candidateId) {
        try {
            ResponseEntity<List<InterviewDto>> response = restTemplate.exchange(
                    UrlConstants.INTERVIEW_SERVICE + "/candidate/{candidateId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    candidateId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load candidate interviews.");
        }
    }

    @Override
    public List<InterviewDto> getRecruiterInterviews(Long recruiterId) {
        try {
            ResponseEntity<List<InterviewDto>> response = restTemplate.exchange(
                    UrlConstants.INTERVIEW_SERVICE + "/recruiter/{recruiterId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {},
                    recruiterId
            );

            return response.getBody() != null
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load recruiter interviews.");
        }
    }

    @Override
    public InterviewDto getInterviewById(Long interviewId) {
        try {
            return restTemplate.getForObject(
                    UrlConstants.INTERVIEW_SERVICE + "/{interviewId}",
                    InterviewDto.class,
                    interviewId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Interview not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to load interview details.");
        }
    }

    @Override
    public void cancelInterview(Long interviewId) {
        try {
            restTemplate.postForObject(
                    UrlConstants.INTERVIEW_SERVICE + "/{interviewId}/cancel",
                    null,
                    Void.class,
                    interviewId
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Interview not found.");
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Unable to cancel interview.");
        }
    }
}