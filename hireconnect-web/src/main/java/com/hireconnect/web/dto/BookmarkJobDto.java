package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkJobDto {

    private Long bookmarkId;
    private Long candidateId;
    private Long jobId;

    private String jobTitle;
    private String companyName;
    private String location;
}