package com.hireconnect.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse {
    private UUID savedJobId;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String type;
    private String status;
    private LocalDateTime savedAt;
}
