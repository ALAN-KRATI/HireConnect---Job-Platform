package com.hireconnect.subscription.repository;

import com.hireconnect.subscription.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findBySubscriptionId(Integer subscriptionId);

    List<Invoice> findByRecruiterId(Integer recruiterId);

    Optional<Invoice> findTopByRecruiterIdOrderByPaymentDateDesc(Integer recruiterId);
}