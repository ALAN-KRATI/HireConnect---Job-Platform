package com.hireconnect.profile.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hireconnect.profile.entity.CandidateProfile;
//import com.hireconnect.profile.enums.UserRole;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID>{
    Optional<CandidateProfile> findByEmail(String email);
    Optional<CandidateProfile> findByMobile(String mobile);
    Optional<CandidateProfile> findByProfileId(UUID profileId);

    //Optional<CandidateProfile> findAllByRole(UserRole role);

    Optional<CandidateProfile> deleteByProfileId(UUID profileId);
}
