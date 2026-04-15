package com.hireconnect.application.dto;

import com.hireconnect.application.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    private UUID applicationId;
    private Long jobId;
    private UUID candidateId;
    private UUID recruiterId;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    private ApplicationStatus status;

    private String coverLetter;
    private String resumeUrl;
}