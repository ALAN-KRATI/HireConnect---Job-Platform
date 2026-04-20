package com.hireconnect.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    
    private String clientSecret;
    
    private String paymentIntentId;
    
    private String checkoutSessionId;
    
    private String checkoutUrl;
    
    private Double amount;
    
    private String currency;
    
    private String status;
    
    private String message;
}
