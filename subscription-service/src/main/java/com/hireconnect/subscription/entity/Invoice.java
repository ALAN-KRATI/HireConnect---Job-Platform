package com.hireconnect.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.hireconnect.subscription.enums.PaymentMode;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    private Integer subscriptionId;

    private Double amount;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String transactionId;

    private Integer recruiterId;
}