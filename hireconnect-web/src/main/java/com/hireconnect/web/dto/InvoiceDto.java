package com.hireconnect.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.hireconnect.web.enums.InvoiceStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {

    private Long id;
    private Long recruiterId;

    private String invoiceNumber;
    private Double amount;

    private InvoiceStatus status; // Paid / Pending / Failed

    private LocalDate issuedDate;
    private LocalDate dueDate;
}