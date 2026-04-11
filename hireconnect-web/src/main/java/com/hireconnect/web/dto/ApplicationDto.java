package com.hireconnect.web.dto;

import com.hireconnect.web.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {

    private Long applicationId;

    private Long candidateId;
    private Long recruiterId;
    private Long jobId;

    private String candidateName;
    private String candidateEmail;
    private String recruiterName;

    private String jobTitle;
    private String companyName;
    private String location;

    private String coverLetter;
    private String resumeUrl;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}