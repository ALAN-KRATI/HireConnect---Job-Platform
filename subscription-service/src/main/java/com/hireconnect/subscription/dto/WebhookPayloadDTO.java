package com.hireconnect.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookPayloadDTO {
    
    private String type;
    
    private DataObject data;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataObject {
        private Object object;
    }
}
