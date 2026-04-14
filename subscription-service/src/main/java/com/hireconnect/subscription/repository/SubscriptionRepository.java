package com.hireconnect.subscription.repository;

import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByRecruiterId(UUID recruiterId);

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findByRecruiterIdAndStatus(
            UUID recruiterId,
            SubscriptionStatus status
    );

    long countByPlan(SubscriptionPlan plan);

    boolean existsByRecruiterIdAndStatus(
            UUID recruiterId,
            SubscriptionStatus status
    );
}