package com.hireconnect.profile.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hireconnect.profile.entity.RecruiterProfile;
//import com.hireconnect.profile.enums.UserRole;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, UUID>{
    Optional<RecruiterProfile> findByEmail(String email);
    Optional<RecruiterProfile> findByMobile(String mobile);
    Optional<RecruiterProfile> findByProfileId(UUID profileId);
   // Optional<RecruiterProfile> findAllByRole(UserRole role);
    Optional<RecruiterProfile> deleteByProfileId(UUID profileId);
}
