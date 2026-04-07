package com.hireconnect.application.controller;

import com.hireconnect.application.dto.StatusUpdateRequest;
import com.hireconnect.application.entity.Application;
import com.hireconnect.application.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationResource {

    private final ApplicationService applicationService;

    public ApplicationResource(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<Application> submitApplication(@RequestBody Application application) {
        return ResponseEntity.ok(applicationService.submitApplication(application));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getByCandidate(candidateId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getByJob(jobId));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getById(@PathVariable Long applicationId) {
        return ResponseEntity.ok(applicationService.getById(applicationId));
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long applicationId, @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(applicationId, request.getStatus()));
    }

    @PutMapping("/{applicationId}/withdraw")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long applicationId) {
        applicationService.withdrawApplication(applicationId);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @GetMapping("/job/{jobId}/count")
    public ResponseEntity<Integer> countByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.countByJob(jobId));
    }

    @GetMapping("/recruiter/{recruiterId}/count")
    public ResponseEntity<Long> getRecruiterApplicationCount(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(20L);
    }

    @GetMapping("/recruiter/{recruiterId}/shortlisted/count")
    public ResponseEntity<Integer> getShortlistedCount(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(4);
    }

    @GetMapping("/recruiter/{recruiterId}/offered/count")
    public ResponseEntity<Integer> getOfferedCount(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(2);
    }

    @GetMapping("/recruiter/{recruiterId}/rejected/count")
    public ResponseEntity<Integer> getRejectedCount(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(7);
    }

    @GetMapping("/recruiter/{recruiterId}/avg-time-to-hire")
    public ResponseEntity<Double> getAverageTimeToHire(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(12.5);
    }

    @GetMapping("/platform/total")
    public ResponseEntity<Long> getPlatformApplicationCount() {
        return ResponseEntity.ok(100L);
    }

    @GetMapping("/platform/shortlisted/count")
    public ResponseEntity<Integer> getPlatformShortlistedCount() {
        return ResponseEntity.ok(25);
    }

    @GetMapping("/platform/offered/count")
    public ResponseEntity<Integer> getPlatformOfferedCount() {
        return ResponseEntity.ok(10);
    }

    @GetMapping("/platform/rejected/count")
    public ResponseEntity<Integer> getPlatformRejectedCount() {
        return ResponseEntity.ok(40);
    }

    @GetMapping("/platform/avg-time-to-hire")
    public ResponseEntity<Double> getPlatformAverageTimeToHire() {
        return ResponseEntity.ok(15.3);
    }
}