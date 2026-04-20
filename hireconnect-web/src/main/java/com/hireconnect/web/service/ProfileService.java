package com.hireconnect.web.service;

import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.RegisterRequest;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    void registerCandidate(RegisterRequest request);

    UUID getCandidateIdByEmail(String email);

    UUID getRecruiterIdByEmail(String email);

    void registerRecruiter(RegisterRequest request);

    ProfileDto getCandidateProfile(UUID candidateId);

    ProfileDto getRecruiterProfile(UUID recruiterId);

    ProfileDto updateProfile(UUID userId, ProfileDto profileDto);

    List<ProfileDto> getAllUsers();

    void activateUser(UUID userId);

    void suspendUser(UUID userId);

    void bookmarkJob(UUID candidateId, Long jobId);

    void addMoneyToWallet(UUID candidateId, Double amount);

    ProfileDto getUserById(UUID userId);

    void updateWalletBalance(UUID userId, Double amount);
}