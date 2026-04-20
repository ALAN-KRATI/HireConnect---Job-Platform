package com.hireconnect.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotNull(message = "Candidate ID is required")
    private UUID candidateId;

    @Email
    @NotNull(message = "Email is required")
    private String candidateEmail;

    @NotNull(message = "Recruiter ID is required")
    private UUID recruiterId;

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    @NotBlank(message = "Resume URL is required")
    private String resumeUrl;
}