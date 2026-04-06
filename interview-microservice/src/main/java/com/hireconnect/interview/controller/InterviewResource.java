package com.hireconnect.interview.controller;

import com.hireconnect.interview.dto.InterviewRequest;
import com.hireconnect.interview.dto.RescheduleRequest;
import com.hireconnect.interview.entity.Interview;
import com.hireconnect.interview.enums.InterviewStatus;
import com.hireconnect.interview.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
public class InterviewResource {

    private final InterviewService interviewService;

    public InterviewResource(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    //@PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public ResponseEntity<Interview> scheduleInterview(
            @RequestBody InterviewRequest request) {

        Interview interview = new Interview();
        interview.setApplicationId(request.getApplicationId());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMode(request.getMode());
        interview.setMeetLink(request.getMeetLink());
        interview.setLocation(request.getLocation());
        interview.setNotes(request.getNotes());

        return ResponseEntity.ok(interviewService.scheduleInterview(interview));
    }

    //@PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.confirmInterview(id));
    }

    //@PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Interview> reschedule(
            @PathVariable Long id,
            @RequestBody RescheduleRequest request) {

        return ResponseEntity.ok(
                interviewService.rescheduleInterview(id,
                        request.getNewDateTime())
        );
    }

    //@PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.cancelInterview(id));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Interview>> getByApplication(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(
                interviewService.getByApplication(applicationId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Interview>> getByStatus(
            @PathVariable InterviewStatus status) {

        return ResponseEntity.ok(interviewService.getByStatus(status));
    }
}