package com.hireconnect.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionAnalyticsDTO {
    
    private Integer totalSubscriptions;
    
    private Integer activeSubscriptions;
    
    private Integer expiredSubscriptions;
    
    private Integer cancelledSubscriptions;
    
    private Double totalRevenue;
    
    private Double revenueThisMonth;
    
    private Double revenueLastMonth;
    
    private Map<String, Integer> planDistribution;
    
    private List<MonthlyRevenueDTO> monthlyRevenue;
    
    private List<RecentInvoiceDTO> recentInvoices;
    
    private RecruiterStatsDTO recruiterStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenueDTO {
        private String month;
        private Double revenue;
        private Integer invoiceCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentInvoiceDTO {
        private Integer invoiceId;
        private String planName;
        private Double amount;
        private String status;
        private String date;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecruiterStatsDTO {
        private UUID recruiterId;
        private String currentPlan;
        private Boolean isActive;
        private Integer daysRemaining;
        private Double totalSpent;
        private Integer totalInvoices;
    }
}
