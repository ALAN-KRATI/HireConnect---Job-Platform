package com.hireconnect.application.dto;

import com.hireconnect.application.enums.ApplicationStatus;

import lombok.Data;

@Data
public class StatusUpdateRequest {
    private ApplicationStatus status;
}
