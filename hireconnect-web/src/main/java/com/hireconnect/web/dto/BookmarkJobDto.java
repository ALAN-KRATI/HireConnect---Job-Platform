package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkJobDto {

    private Long candidateId;
    private Long jobId;
}