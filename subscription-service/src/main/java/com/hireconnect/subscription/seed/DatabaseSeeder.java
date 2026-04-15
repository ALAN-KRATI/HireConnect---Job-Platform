package com.hireconnect.subscription.seed;

import com.hireconnect.subscription.entity.Subscription;
import com.hireconnect.subscription.enums.SubscriptionPlan;
import com.hireconnect.subscription.enums.SubscriptionStatus;
import com.hireconnect.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        if (subscriptionRepository.count() == 0) {
            log.info("Seeding database with sample subscriptions...");
            seedSubscriptions();
            log.info("Subscription seeding completed!");
        } else {
            log.info("Database already contains subscriptions, skipping seeding.");
        }
    }

    private void seedSubscriptions() {
        // Give Rajesh Kumar a PROFESSIONAL subscription
        UUID recruiter1 = UUID.fromString("602933df-bdc5-40e7-b9db-7ced2d14ede1");
        
        Subscription rajeshSubscription = Subscription.builder()
                .recruiterId(recruiter1)
                .plan(SubscriptionPlan.PROFESSIONAL)
                .startDate(LocalDate.now().minusDays(15))
                .endDate(LocalDate.now().plusDays(15))
                .status(SubscriptionStatus.ACTIVE)
                .amountPaid(999.0)
                .build();

        // Give Priya Sharma a FREE subscription
        UUID recruiter2 = UUID.fromString("702933df-bdc5-40e7-b9db-7ced2d14ede2");
        
        Subscription priyaSubscription = Subscription.builder()
                .recruiterId(recruiter2)
                .plan(SubscriptionPlan.FREE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .amountPaid(0.0)
                .build();

        subscriptionRepository.save(rajeshSubscription);
        subscriptionRepository.save(priyaSubscription);

        log.info("Seeded 2 subscriptions (1 PROFESSIONAL, 1 FREE)");
    }
}