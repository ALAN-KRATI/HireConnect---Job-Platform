package com.hireconnect.web.service;

import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.RegisterRequest;

import java.util.List;

public interface ProfileService {

    void registerCandidate(RegisterRequest request);

    Long getCandidateIdByEmail(String email);

    Long getRecruiterIdByEmail(String email);

    void registerRecruiter(RegisterRequest request);

    ProfileDto getCandidateProfile(Long candidateId);

    ProfileDto getRecruiterProfile(Long recruiterId);

    ProfileDto updateProfile(Long userId, ProfileDto profileDto);

    List<ProfileDto> getAllUsers();

    void activateUser(Long userId);

    void suspendUser(Long userId);

    void bookmarkJob(Long candidateId, Long jobId);

    void addMoneyToWallet(Long candidateId, Double amount);

    ProfileDto getUserById(Long userId);

    void updateWalletBalance(Long userId, Double amount);
}