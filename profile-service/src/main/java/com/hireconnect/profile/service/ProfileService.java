package com.hireconnect.profile.service;

import java.util.List;
import java.util.UUID;

import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;

public interface ProfileService {
    CandidateProfile addCandidateProfile(CandidateProfile cp);
    RecruiterProfile addRecruiterProfile(RecruiterProfile rp);

    CandidateProfile updateCandidateProfile(UUID profileId, CandidateProfile cp);
    RecruiterProfile updateRecruiterProfile(UUID profileId, RecruiterProfile profile);

    void deleteCandidateProfile(UUID id);
    void deleteRecruiterProfile(UUID id);

    CandidateProfile getCandidateProfile(UUID id);
    RecruiterProfile getRecruiterProfile(UUID id);

    CandidateProfile getCandidateProfileByEmail(String email);
    RecruiterProfile getRecruiterProfileByEmail(String email);

    List<CandidateProfile> getAllCandidateProfiles();
    List<RecruiterProfile> getAllRecruiterProfiles();

}
