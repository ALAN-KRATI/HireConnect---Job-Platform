package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.hireconnect.web.enums.InterviewStatus;
import com.hireconnect.web.enums.InterviewType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDto {

    private Long id;
    private Long applicationId;

    private String candidateName;
    private String recruiterName;
    private InterviewType interviewType; // Online / Offline

    private String meetingLink;
    private String location;

    private LocalDateTime scheduledAt;
    private InterviewStatus status;
}