package com.hireconnect.profile.service;

import com.hireconnect.profile.dto.ProfileRequest;
import com.hireconnect.profile.dto.ProfileResponse;
import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;
import com.hireconnect.profile.exception.ProfileNotFoundException;
import com.hireconnect.profile.repository.CandidateProfileRepository;
import com.hireconnect.profile.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {

    private final CandidateProfileRepository candidateRepository;
    private final RecruiterProfileRepository recruiterRepository;

    @Override
    public void createDefaultProfile(
            UUID userId,
            String email,
            String role,
            String mobile) {

        if ("CANDIDATE".equalsIgnoreCase(role)) {

            if (candidateRepository.existsByUserId(userId)) {
                return;
            }

            CandidateProfile profile = CandidateProfile.builder()
                    .headline("")
                    .location("")
                    .bio("")
                    .skills(List.of())
                    .experience(0)
                    .dob(LocalDate.of(2000, 1, 1))
                    .gender("Not Specified")
                    .resumeUrl("")
                    .addresses(List.of())
                    .build();

            profile.setUserId(userId);
            profile.setEmail(email);
            profile.setMobile(mobile);
            profile.setFullName("New Candidate");
            profile.setActive(true);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());

            candidateRepository.save(profile);
        }

        if ("RECRUITER".equalsIgnoreCase(role)) {

            if (recruiterRepository.existsByUserId(userId)) {
                return;
            }

            RecruiterProfile profile = RecruiterProfile.builder()
                    .companyName("")
                    .companySize("")
                    .industry("")
                    .website("")
                    .officeLocation("")
                    .build();

            profile.setUserId(userId);
            profile.setEmail(email);
            profile.setMobile(mobile);
            profile.setFullName("New Recruiter");
            profile.setActive(true);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());

            recruiterRepository.save(profile);
        }
    }

    @Override
    public ProfileResponse getCandidateProfile(UUID userId) {

        CandidateProfile profile = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(
                        "Candidate profile not found for userId: " + userId));

        return mapCandidate(profile);
    }

    @Override
    public ProfileResponse getRecruiterProfile(UUID userId) {

        RecruiterProfile profile = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(
                        "Recruiter profile not found for userId: " + userId));

        return mapRecruiter(profile);
    }

    @Override
    public ProfileResponse updateCandidateProfile(UUID userId, ProfileRequest request) {

        CandidateProfile profile = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(
                        "Candidate profile not found for userId: " + userId));

        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
        profile.setMobile(request.getMobile());
        profile.setHeadline(request.getHeadline());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());

        profile.setSkills(
                request.getSkills() == null || request.getSkills().isBlank()
                        ? new java.util.ArrayList<>()
                        : new java.util.ArrayList<>(
                                java.util.Arrays.asList(
                                        request.getSkills().split("\\s*,\\s*"))));

        profile.setExperience(
                request.getExperience() == null || request.getExperience().isBlank()
                        ? 0
                        : Integer.parseInt(request.getExperience()));

        if (profile.getAddresses() == null) {
            profile.setAddresses(new java.util.ArrayList<>());
        }

        profile.setUpdatedAt(LocalDateTime.now());

        CandidateProfile updatedProfile = candidateRepository.save(profile);

        return mapCandidate(updatedProfile);
    }

    @Override
    public ProfileResponse updateRecruiterProfile(UUID userId, ProfileRequest request) {

        RecruiterProfile profile = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(
                        "Recruiter profile not found for userId: " + userId));

        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
        profile.setMobile(request.getMobile());
        profile.setCompanyName(request.getCompanyName());
        profile.setWebsite(request.getCompanyWebsite());
        profile.setIndustry(request.getHeadline());
        profile.setOfficeLocation(request.getLocation());
        profile.setUpdatedAt(LocalDateTime.now());

        return mapRecruiter(recruiterRepository.save(profile));
    }

    @Override
    public void deleteCandidateProfile(UUID userId) {

        if (!candidateRepository.existsByUserId(userId)) {
            throw new ProfileNotFoundException(
                    "Candidate profile not found for userId: " + userId);
        }

        candidateRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteRecruiterProfile(UUID userId) {

        if (!recruiterRepository.existsByUserId(userId)) {
            throw new ProfileNotFoundException(
                    "Recruiter profile not found for userId: " + userId);
        }

        recruiterRepository.deleteByUserId(userId);
    }

    @Override
    public List<ProfileResponse> getAllCandidateProfiles() {
        return candidateRepository.findAll()
                .stream()
                .map(this::mapCandidate)
                .toList();
    }

    @Override
    public List<ProfileResponse> getAllRecruiterProfiles() {
        return recruiterRepository.findAll()
                .stream()
                .map(this::mapRecruiter)
                .toList();
    }

    private ProfileResponse mapCandidate(CandidateProfile profile) {
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .mobile(profile.getMobile())
                .role("CANDIDATE")
                .headline(profile.getHeadline())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .skills(profile.getSkills() == null
                        ? ""
                        : String.join(", ", profile.getSkills()))
                .experience(profile.getExperience() == null
                        ? "0"
                        : String.valueOf(profile.getExperience()))
                .active(profile.isActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private ProfileResponse mapRecruiter(RecruiterProfile profile) {
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .mobile(profile.getMobile())
                .role("RECRUITER")
                .companyName(profile.getCompanyName())
                .companyWebsite(profile.getWebsite())
                .headline(profile.getIndustry())
                .location(profile.getOfficeLocation())
                .active(profile.isActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}