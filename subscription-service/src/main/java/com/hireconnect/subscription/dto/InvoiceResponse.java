package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.PaymentMode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvoiceResponse {

    private Integer invoiceId;
    private Integer subscriptionId;
    private UUID recruiterId;
    private Double amount;
    private PaymentMode paymentMode;
    private String transactionId;
    private LocalDateTime paymentDate;
}