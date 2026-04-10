package com.hireconnect.application.controller;

import com.hireconnect.application.dto.ApplicationResponse;
import com.hireconnect.application.dto.StatusUpdateRequest;
import com.hireconnect.application.entity.Application;
import com.hireconnect.application.enums.ApplicationStatus;
import com.hireconnect.application.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationResource {

        private final ApplicationService applicationService;

        public ApplicationResource(ApplicationService applicationService) {
                this.applicationService = applicationService;
        }

        @PostMapping
        public ResponseEntity<ApplicationResponse> submitApplication(@RequestBody Application application) {
                Application saved = applicationService.submitApplication(application);
                return ResponseEntity.ok(mapToResponse(saved));
        }

        @GetMapping("/candidate/{candidateId}")
        public ResponseEntity<List<ApplicationResponse>> getByCandidate(@PathVariable UUID candidateId) {
                List<ApplicationResponse> responses = applicationService.getByCandidate(candidateId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                return ResponseEntity.ok(responses);
        }

        @GetMapping("/job/{jobId}")
        public ResponseEntity<List<ApplicationResponse>> getByJob(@PathVariable Long jobId) {
                List<ApplicationResponse> responses = applicationService.getByJob(jobId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                return ResponseEntity.ok(responses);
        }

        @GetMapping("/{applicationId}")
        public ResponseEntity<ApplicationResponse> getById(@PathVariable UUID applicationId) {
                return ResponseEntity.ok(
                                mapToResponse(applicationService.getById(applicationId)));
        }

        @PutMapping("/{applicationId}/status")
        public ResponseEntity<ApplicationResponse> updateStatus(
                        @PathVariable UUID applicationId,
                        @RequestBody StatusUpdateRequest request) {

                return ResponseEntity.ok(
                                mapToResponse(
                                                applicationService.updateStatus(applicationId, request.getStatus())));
        }

        @PutMapping("/{applicationId}/withdraw")
        public ResponseEntity<String> withdrawApplication(@PathVariable UUID applicationId) {
                applicationService.withdrawApplication(applicationId);
                return ResponseEntity.ok("Application withdrawn successfully");
        }

        @PostMapping("/{applicationId}/shortlist")
        public ResponseEntity<ApplicationResponse> shortlistCandidate(@PathVariable UUID applicationId) {
                return ResponseEntity.ok(
                                mapToResponse(
                                                applicationService.updateStatus(
                                                                applicationId,
                                                                ApplicationStatus.SHORTLISTED)));
        }

        @PostMapping("/{applicationId}/reject")
        public ResponseEntity<ApplicationResponse> rejectCandidate(@PathVariable UUID applicationId) {
                return ResponseEntity.ok(
                                mapToResponse(
                                                applicationService.updateStatus(
                                                                applicationId,
                                                                ApplicationStatus.REJECTED)));
        }

        @PostMapping("/{applicationId}/advance")
        public ResponseEntity<ApplicationResponse> advanceCandidate(@PathVariable UUID applicationId) {
                return ResponseEntity.ok(
                                mapToResponse(
                                                applicationService.updateStatus(
                                                                applicationId,
                                                                ApplicationStatus.INTERVIEW_SCHEDULED)));
        }

        @GetMapping("/job/{jobId}/count")
        public ResponseEntity<Long> countByJob(@PathVariable Long jobId) {
                return ResponseEntity.ok(applicationService.countByJob(jobId));
        }

        @GetMapping("/recruiter/{recruiterId}/count")
        public ResponseEntity<Long> getRecruiterApplicationCount(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(applicationService.countByRecruiterId(recruiterId));
        }

        @GetMapping("/recruiter/{recruiterId}/shortlisted/count")
        public ResponseEntity<Long> getShortlistedCount(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(
                                applicationService.countByRecruiterIdAndStatus(
                                                recruiterId,
                                                ApplicationStatus.SHORTLISTED));
        }

        @GetMapping("/recruiter/{recruiterId}/offered/count")
        public ResponseEntity<Long> getOfferedCount(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(
                                applicationService.countByRecruiterIdAndStatus(
                                                recruiterId,
                                                ApplicationStatus.OFFERED));
        }

        @GetMapping("/recruiter/{recruiterId}/rejected/count")
        public ResponseEntity<Long> getRejectedCount(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(
                                applicationService.countByRecruiterIdAndStatus(
                                                recruiterId,
                                                ApplicationStatus.REJECTED));
        }

        @GetMapping("/recruiter/{recruiterId}/avg-time-to-hire")
        public ResponseEntity<Double> getAverageTimeToHire(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(
                                applicationService.findAverageTimeToHireByRecruiterId(recruiterId));
        }

        @GetMapping("/platform/total")
        public ResponseEntity<Long> getPlatformApplicationCount() {
                return ResponseEntity.ok(applicationService.count());
        }

        @GetMapping("/platform/shortlisted/count")
        public ResponseEntity<Long> getPlatformShortlistedCount() {
                return ResponseEntity.ok(
                                applicationService.countByStatus(ApplicationStatus.SHORTLISTED));
        }

        @GetMapping("/platform/offered/count")
        public ResponseEntity<Long> getPlatformOfferedCount() {
                return ResponseEntity.ok(
                                applicationService.countByStatus(ApplicationStatus.OFFERED));
        }

        @GetMapping("/platform/rejected/count")
        public ResponseEntity<Long> getPlatformRejectedCount() {
                return ResponseEntity.ok(
                                applicationService.countByStatus(ApplicationStatus.REJECTED));
        }

        @GetMapping("/platform/avg-time-to-hire")
        public ResponseEntity<Double> getPlatformAverageTimeToHire() {
                return ResponseEntity.ok(applicationService.findPlatformAverageTimeToHire());
        }

        private ApplicationResponse mapToResponse(Application application) {
                return new ApplicationResponse(
                                application.getApplicationId(),
                                application.getJobId(),
                                application.getCandidateId(),
                                application.getRecruiterId(),
                                application.getAppliedAt(),
                                application.getUpdatedAt(),
                                application.getStatus(),
                                application.getCoverLetter(),
                                application.getResumeUrl());
        }

        @GetMapping("/recruiter/{recruiterId}/interview-scheduled/count")
        public ResponseEntity<Long> getInterviewScheduledCount(@PathVariable Long recruiterId) {
                return ResponseEntity.ok(
                                applicationService.countByRecruiterIdAndStatus(
                                                recruiterId,
                                                ApplicationStatus.INTERVIEW_SCHEDULED));
        }

        @GetMapping("/platform/interview-scheduled/count")
        public ResponseEntity<Long> getPlatformInterviewScheduledCount() {
                return ResponseEntity.ok(
                        applicationService.countByStatus(ApplicationStatus.INTERVIEW_SCHEDULED)
                );
        }
}