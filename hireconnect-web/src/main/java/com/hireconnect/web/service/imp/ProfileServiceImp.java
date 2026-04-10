package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.ProfileService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class ProfileServiceImp implements ProfileService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://profile-service/profiles";

    public ProfileServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void registerCandidate(RegisterRequest request) {

        restTemplate.postForObject(
                BASE_URL + "/candidate",
                request,
                Void.class
        );
    }

    @Override
    public void registerRecruiter(RegisterRequest request) {

        restTemplate.postForObject(
                BASE_URL + "/recruiter",
                request,
                Void.class
        );
    }

    @Override
    public ProfileDto getCandidateProfile(Long candidateId) {

        return restTemplate.getForObject(
                BASE_URL + "/candidate/{candidateId}",
                ProfileDto.class,
                candidateId
        );
    }

    @Override
    public ProfileDto getRecruiterProfile(Long recruiterId) {

        return restTemplate.getForObject(
                BASE_URL + "/recruiter/{recruiterId}",
                ProfileDto.class,
                recruiterId
        );
    }

    @Override
    public ProfileDto updateProfile(Long userId, ProfileDto dto) {

        restTemplate.put(
                BASE_URL + "/{userId}",
                dto,
                userId
        );

        return getUserById(userId);
    }

    @Override
    public List<ProfileDto> getAllUsers() {

        ResponseEntity<List<ProfileDto>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProfileDto>>() {}
        );

        return response.getBody() != null
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public void suspendUser(Long userId) {

        restTemplate.postForObject(
                BASE_URL + "/{userId}/suspend",
                null,
                Void.class,
                userId
        );
    }

    @Override
    public void activateUser(Long userId) {

        restTemplate.postForObject(
                BASE_URL + "/{userId}/activate",
                null,
                Void.class,
                userId
        );
    }

    @Override
    public void bookmarkJob(Long candidateId, Long jobId) {

        restTemplate.postForObject(
                BASE_URL + "/{candidateId}/bookmark/{jobId}",
                null,
                Void.class,
                candidateId,
                jobId
        );
    }

    @Override
    public void addMoneyToWallet(Long candidateId, Double amount) {

        restTemplate.postForObject(
                BASE_URL + "/{candidateId}/wallet?amount={amount}",
                null,
                Void.class,
                candidateId,
                amount
        );
    }

    @Override
    public ProfileDto getUserById(Long userId) {

        return restTemplate.getForObject(
                BASE_URL + "/{userId}",
                ProfileDto.class,
                userId
        );
    }

    @Override
    public void updateWalletBalance(Long userId, Double amount) {

        restTemplate.postForObject(
                BASE_URL + "/{userId}/wallet/update?amount={amount}",
                null,
                Void.class,
                userId,
                amount
        );
    }

    @Override
    public Long getCandidateIdByEmail(String email) {

        return restTemplate.getForObject(
                BASE_URL + "/candidate/email/{email}",
                Long.class,
                email
        );
    }

    @Override
    public Long getRecruiterIdByEmail(String email) {

        return restTemplate.getForObject(
                BASE_URL + "/recruiter/email/{email}",
                Long.class,
                email
        );
    }
}