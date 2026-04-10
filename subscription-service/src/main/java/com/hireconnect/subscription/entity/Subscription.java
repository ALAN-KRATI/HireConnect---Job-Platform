package com.hireconnect.subscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subscriptionId;

    private Integer recruiterId;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan; // FREE, PROFESSIONAL, ENTERPRISE

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // ACTIVE, CANCELLED, EXPIRED

    private Double amountPaid;

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE
                && endDate != null
                && !endDate.isBefore(LocalDate.now());
    }
}