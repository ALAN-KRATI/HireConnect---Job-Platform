package com.hireconnect.job.controller;

import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;
import com.hireconnect.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobResource {

    private final JobService service;

    @PostMapping
    public ResponseEntity<JobResponse> addJob(@RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addJob(request));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(service.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getJobById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<JobResponse>> getJobsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getJobsByCategory(category));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<JobResponse>> getJobsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(service.getJobsByLocation(location));
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) Integer experience
    ) {
        return ResponseEntity.ok(
                service.searchJobs(title, location, category, minSalary, maxSalary, experience)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(service.updateJob(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) {
        service.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobResponse> changeStatus(
            @PathVariable("id") Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(service.changeStatus(id, status));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<JobResponse>> getJobsByRecruiter(
            @PathVariable("recruiterId") Long recruiterId
    ) {
        return ResponseEntity.ok(service.getJobsByRecruiter(recruiterId));
    }
}