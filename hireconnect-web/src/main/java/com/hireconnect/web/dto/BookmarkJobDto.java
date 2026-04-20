package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkJobDto {

    private Long bookmarkId;
    private UUID candidateId;
    private Long jobId;

    private String jobTitle;
    private String companyName;
    private String location;
}