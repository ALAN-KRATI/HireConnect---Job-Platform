package com.hireconnect.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse {
    private UUID savedJobId;
    private Long jobId;

    // Expose both jobTitle and title so existing callers keep working and the
    // candidate saved-jobs UI (which reads job.title) also has a value.
    private String jobTitle;
    private String title;

    private String companyName;
    private String location;
    private String type;
    private String status;
    private List<String> skills;
    private Integer experienceRequired;
    private Double minSalary;
    private Double maxSalary;
    private LocalDateTime savedAt;
}
