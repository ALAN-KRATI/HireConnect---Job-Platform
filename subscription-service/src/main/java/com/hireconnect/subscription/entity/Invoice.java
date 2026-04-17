package com.hireconnect.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hireconnect.subscription.enums.PaymentMode;
import com.hireconnect.subscription.enums.PaymentStatus;
import com.hireconnect.subscription.enums.SubscriptionPlan;

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

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId;

    private String stripePaymentIntentId;

    private String stripeCheckoutSessionId;

    private UUID recruiterId;

    @Column(length = 500)
    private String invoiceUrl;

    @Column(length = 500)
    private String receiptUrl;

    @Column(length = 1000)
    private String failureReason;

    @PrePersist
    public void prePersist() {
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan planType;
}