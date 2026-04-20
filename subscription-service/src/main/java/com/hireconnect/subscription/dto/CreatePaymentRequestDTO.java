package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequestDTO {
    
    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;
    
    private String successUrl;
    
    private String cancelUrl;
}
