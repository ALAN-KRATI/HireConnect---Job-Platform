package com.hireconnect.subscription.repository;

import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.SubscriptionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByRecruiterId(Integer recruiterId);

    List<Subscription> findByStatus(String status);

    Optional<Subscription> findByRecruiterIdAndStatus(Integer recruiterId, SubscriptionStatus status);

    long countByPlan(String plan);
}