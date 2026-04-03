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
}