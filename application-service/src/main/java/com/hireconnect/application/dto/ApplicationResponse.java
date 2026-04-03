package com.hireconnect.application.dto;

import java.time.LocalDateTime;

import com.hireconnect.application.enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private Long jobId;
    private Long candidateId;
    private LocalDateTime appliedAt;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
}
