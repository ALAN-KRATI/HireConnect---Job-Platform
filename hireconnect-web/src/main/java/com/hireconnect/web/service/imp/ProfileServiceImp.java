package com.hireconnect.web.service.imp;

import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProfileServiceImp implements ProfileService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8082/profiles";

    @Override
    public void registerCandidate(RegisterRequest request) {
        restTemplate.postForObject(BASE_URL + "/candidate", request, Void.class);
    }

    @Override
    public void registerRecruiter(RegisterRequest request) {
        restTemplate.postForObject(BASE_URL + "/recruiter", request, Void.class);
    }

    @Override
    public ProfileDto getCandidateProfile(Long id) {
        return restTemplate.getForObject(BASE_URL + "/candidate/" + id, ProfileDto.class);
    }

    @Override
    public ProfileDto getRecruiterProfile(Long id) {
        return restTemplate.getForObject(BASE_URL + "/recruiter/" + id, ProfileDto.class);
    }

    @Override
    public ProfileDto updateProfile(Long id, ProfileDto dto) {
        restTemplate.put(BASE_URL + "/" + id, dto);
        return dto;
    }

    @Override
    public List<ProfileDto> getAllUsers() {
        ProfileDto[] response = restTemplate.getForObject(BASE_URL, ProfileDto[].class);
        return Arrays.asList(response);
    }

    @Override
    public void suspendUser(Long userId) {
        restTemplate.postForObject(BASE_URL + "/" + userId + "/suspend", null, Void.class);
    }

    @Override
    public void bookmarkJob(Long candidateId, Long jobId) {
        restTemplate.postForObject(BASE_URL + "/" + candidateId + "/bookmark/" + jobId, null, Void.class);
    }

    @Override
    public void addMoneyToWallet(Long candidateId, Double amount) {
        restTemplate.postForObject(BASE_URL + "/" + candidateId + "/wallet?amount=" + amount, null, Void.class);
    }

    @Override
    public ProfileDto getUserById(Long userId) {
        return restTemplate.getForObject(
                BASE_URL + "/" + userId,
                ProfileDto.class);
    }

    @Override
    public void updateWalletBalance(Long userId, Double amount) {
        restTemplate.postForObject(
                BASE_URL + "/" + userId + "/wallet/update?amount=" + amount,
                null,
                Void.class);
    }
}