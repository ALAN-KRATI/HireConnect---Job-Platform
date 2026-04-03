package com.hireconnect.profile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hireconnect.profile.entity.CandidateProfile;
import com.hireconnect.profile.entity.RecruiterProfile;
import com.hireconnect.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileResource {

    private final ProfileService service;

    @PostMapping("/candidates")
    public ResponseEntity<CandidateProfile> addCandidateProfile(
            @Valid @RequestBody CandidateProfile profile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addCandidateProfile(profile));
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateProfile> updateCandidateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody CandidateProfile profile) {
        return ResponseEntity.ok(service.updateCandidateProfile(id, profile));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<String> deleteCandidateProfile(@PathVariable UUID id) {
        service.deleteCandidateProfile(id);
        return ResponseEntity.ok("Candidate Profile deleted successfully!");
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateProfile> getCandidateProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getCandidateProfile(id));
    }

    @GetMapping("/candidates/email/{email}")
    public ResponseEntity<CandidateProfile> getCandidateProfileByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getCandidateProfileByEmail(email));
    }

    @GetMapping("/candidates/all")
    public ResponseEntity<List<CandidateProfile>> getAllCandidateProfiles() {
        return ResponseEntity.ok(service.getAllCandidateProfiles());
    }

    @PostMapping("/recruiters")
    public ResponseEntity<RecruiterProfile> addRecruiterProfile(
            @Valid @RequestBody RecruiterProfile profile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addRecruiterProfile(profile));
    }

    @PutMapping("/recruiters/{Id}")
    public ResponseEntity<RecruiterProfile> updateRecruiterProfile(
            @PathVariable UUID Id,
            @Valid @RequestBody RecruiterProfile profile) {
        return ResponseEntity.ok(service.updateRecruiterProfile(Id, profile));
    }

    @DeleteMapping("/recruiters/{Id}")
    public ResponseEntity<String> deleteRecruiterProfile(@PathVariable UUID Id) {
        service.deleteRecruiterProfile(Id);
        return ResponseEntity.ok("Recruiter profile deleted successfully.");
    }

    @GetMapping("/recruiters/{Id}")
    public ResponseEntity<RecruiterProfile> getRecruiterProfile(@PathVariable UUID Id) {
        return ResponseEntity.ok(service.getRecruiterProfile(Id));
    }

    @GetMapping("/recruiters/email/{email}")
    public ResponseEntity<RecruiterProfile> getRecruiterProfileByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getRecruiterProfileByEmail(email));
    }

    @GetMapping("/recruiters")
    public ResponseEntity<List<RecruiterProfile>> getAllRecruiterProfiles() {
        return ResponseEntity.ok(service.getAllRecruiterProfiles());
    }
}