package com.hireconnect.subscription.dto;

import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {
    
    private Integer invoiceId;
    
    private Integer subscriptionId;
    
    private String planName;
    
    private Double amount;
    
    private String currency;
    
    private LocalDateTime paymentDate;
    
    private PaymentMode paymentMode;
    
    private PaymentStatus paymentStatus;
    
    private String transactionId;
    
    private String stripePaymentIntentId;
    
    private String invoiceUrl;
    
    private String receiptUrl;
    
    private LocalDateTime createdAt;
    
    private String pdfDownloadUrl;
}
