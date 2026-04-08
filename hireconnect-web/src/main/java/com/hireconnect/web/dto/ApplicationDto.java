package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.hireconnect.web.enums.ApplicationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {

    private Long id;
    private Long candidateId;
    private Long jobId;

    private String candidateName;
    private String jobTitle;
    private String companyName;

    private String coverLetter;
    private String resumeUrl;

    private ApplicationStatus status; // Applied, Shortlisted, Rejected, Offered

    private LocalDateTime appliedAt;
}