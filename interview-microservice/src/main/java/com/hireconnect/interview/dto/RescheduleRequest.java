package com.hireconnect.interview.dto;

import java.time.LocalDateTime;

public class RescheduleRequest {

    private LocalDateTime newDateTime;

    public LocalDateTime getNewDateTime() {
        return newDateTime;
    }

    public void setNewDateTime(LocalDateTime newDateTime) {
        this.newDateTime = newDateTime;
    }
}