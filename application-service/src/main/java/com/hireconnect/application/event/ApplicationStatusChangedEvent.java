package com.hireconnect.application.event;

import lombok.Data;

@Data
public class ApplicationStatusChangedEvent {

    private Long applicationId;
    private Long candidateId;
    private String status;

    public ApplicationStatusChangedEvent(Long applicationId, Long candidateId, String status) {
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.status = status;
    }

}
