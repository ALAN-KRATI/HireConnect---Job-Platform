package com.hireconnect.web.dto;

import com.hireconnect.web.enums.InterviewStatus;
import com.hireconnect.web.enums.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDto {

    private Long interviewId;
    private Long applicationId;
    private Long candidateId;
    private Long recruiterId;
    private Long jobId;

    private String candidateName;
    private String candidateEmail;

    private String recruiterName;
    private String recruiterEmail;

    private String jobTitle;
    private String companyName;

    private InterviewType interviewType;

    private String meetingLink;
    private String location;

    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;

    private InterviewStatus status;

    private String notes;
}