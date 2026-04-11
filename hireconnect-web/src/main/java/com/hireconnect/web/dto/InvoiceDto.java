package com.hireconnect.web.dto;

import com.hireconnect.web.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Long invoiceId;
    private Long recruiterId;

    private String recruiterName;
    private String invoiceNumber;

    private Double amount;
    private String paymentMethod;
    private String subscriptionPlan;

    private InvoiceStatus status;

    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate paidDate;
}