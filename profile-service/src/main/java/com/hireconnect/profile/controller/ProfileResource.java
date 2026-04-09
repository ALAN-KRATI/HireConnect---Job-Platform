package com.hireconnect.profile.controller;

import com.hireconnect.profile.dto.ProfileRequest;
import com.hireconnect.profile.dto.ProfileResponse;
import com.hireconnect.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileResource {

    private final ProfileService profileService;

    @GetMapping("/candidates/{userId}")
    public ResponseEntity<ProfileResponse> getCandidateProfile(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                profileService.getCandidateProfile(userId)
        );
    }

    @PutMapping("/candidates/{userId}")
    public ResponseEntity<ProfileResponse> updateCandidateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody ProfileRequest request) {

        return ResponseEntity.ok(
                profileService.updateCandidateProfile(userId, request)
        );
    }

    @DeleteMapping("/candidates/{userId}")
    public ResponseEntity<String> deleteCandidateProfile(
            @PathVariable UUID userId) {

        profileService.deleteCandidateProfile(userId);

        return ResponseEntity.ok("Candidate profile deleted successfully");
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<ProfileResponse>> getAllCandidateProfiles() {

        return ResponseEntity.ok(
                profileService.getAllCandidateProfiles()
        );
    }

    @GetMapping("/recruiters/{userId}")
    public ResponseEntity<ProfileResponse> getRecruiterProfile(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                profileService.getRecruiterProfile(userId)
        );
    }

    @PutMapping("/recruiters/{userId}")
    public ResponseEntity<ProfileResponse> updateRecruiterProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody ProfileRequest request) {

        return ResponseEntity.ok(
                profileService.updateRecruiterProfile(userId, request)
        );
    }

    @DeleteMapping("/recruiters/{userId}")
    public ResponseEntity<String> deleteRecruiterProfile(
            @PathVariable UUID userId) {

        profileService.deleteRecruiterProfile(userId);

        return ResponseEntity.ok("Recruiter profile deleted successfully");
    }

    @GetMapping("/recruiters")
    public ResponseEntity<List<ProfileResponse>> getAllRecruiterProfiles() {

        return ResponseEntity.ok(
                profileService.getAllRecruiterProfiles()
        );
    }
}