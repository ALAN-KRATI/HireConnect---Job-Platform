package com.hireconnect.interview.dto;

import java.time.LocalDateTime;

import com.hireconnect.interview.enums.InterviewMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRequest {

    private Long applicationId;
    private LocalDateTime scheduledAt;
    private InterviewMode mode;
    private String meetLink;
    private String location;
    private String notes;

    
}