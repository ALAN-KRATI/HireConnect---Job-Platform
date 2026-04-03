package com.hireconnect.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {
    private Long jobId;
    private Long candidateId;
    private String coverLetter;
    private String resumeUrl;
}
