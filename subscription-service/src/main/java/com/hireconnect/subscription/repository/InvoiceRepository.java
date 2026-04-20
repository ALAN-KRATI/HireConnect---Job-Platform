package com.hireconnect.subscription.repository;

import com.hireconnect.subscription.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findBySubscriptionId(Integer subscriptionId);

    List<Invoice> findByRecruiterId(UUID recruiterId);

    Optional<Invoice> findTopByRecruiterIdOrderByPaymentDateDesc(UUID recruiterId);

    Optional<Invoice> findByStripePaymentIntentId(String paymentIntentId);

    Optional<Invoice> findByStripeCheckoutSessionId(String checkoutSessionId);
}