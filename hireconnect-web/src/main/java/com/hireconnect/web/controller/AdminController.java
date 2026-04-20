package com.hireconnect.web.controller;

import com.hireconnect.web.dto.AnalyticsDto;
import com.hireconnect.web.dto.ProfileDto;
import com.hireconnect.web.dto.SubscriptionDto;
import com.hireconnect.web.enums.UserRole;
import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProfileService profileService;
    private final AnalyticsService analyticsService;
    private final SubscriptionService subscriptionService;
    private final JobService jobService;

    public AdminController(ProfileService profileService,
                           AnalyticsService analyticsService,
                           SubscriptionService subscriptionService,
                           JobService jobService) {
        this.profileService = profileService;
        this.analyticsService = analyticsService;
        this.subscriptionService = subscriptionService;
        this.jobService = jobService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        List<ProfileDto> users = profileService.getAllUsers();
        AnalyticsDto analytics = analyticsService.getPlatformAnalytics();
        List<SubscriptionDto> subscriptions = subscriptionService.getAllSubscriptions();

        long totalCandidates = users.stream()
                .filter(user -> user.getRole() == UserRole.CANDIDATE)
                .count();

        long totalRecruiters = users.stream()
                .filter(user -> user.getRole() == UserRole.RECRUITER)
                .count();

        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("users", users);
        model.addAttribute("jobs", jobService.getAllJobs());
        model.addAttribute("analytics", analytics);
        model.addAttribute("subscriptions", subscriptions);

        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalCandidates", totalCandidates);
        model.addAttribute("totalRecruiters", totalRecruiters);
        model.addAttribute("totalJobs", analytics.getTotalJobs());
        model.addAttribute("totalApplications", analytics.getTotalApplications());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {

        model.addAttribute("pageTitle", "Manage Users");
        model.addAttribute("users", profileService.getAllUsers());

        return "admin/users";
    }

    @PostMapping("/users/{userId}/suspend")
    public String suspendUser(@PathVariable UUID userId) {

        profileService.suspendUser(userId);

        return "redirect:/admin/users?success=User suspended successfully";
    }

    @PostMapping("/users/{userId}/activate")
    public String activateUser(@PathVariable UUID userId) {

        profileService.activateUser(userId);

        return "redirect:/admin/users?success=User activated successfully";
    }

    @GetMapping("/jobs")
    public String viewAllJobs(Model model) {

        model.addAttribute("pageTitle", "Manage Jobs");
        model.addAttribute("jobs", jobService.getAllJobs());

        return "admin/jobs";
    }

    @PostMapping("/jobs/{jobId}/delete")
    public String deleteJob(@PathVariable Long jobId) {

        jobService.deleteJob(jobId);

        return "redirect:/admin/jobs?success=Job deleted successfully";
    }

    @GetMapping("/analytics")
    public String viewPlatformAnalytics(Model model) {

        model.addAttribute("pageTitle", "Platform Analytics");
        model.addAttribute("analytics", analyticsService.getPlatformAnalytics());

        return "admin/analytics";
    }

    @GetMapping("/subscriptions")
    public String manageSubscriptions(Model model) {

        model.addAttribute("pageTitle", "Manage Subscriptions");
        model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());

        return "admin/subscriptions";
    }

    @GetMapping("/invoices")
    public String viewAllInvoices(Model model) {

        model.addAttribute("pageTitle", "All Invoices");
        model.addAttribute("invoices", subscriptionService.getAllInvoices());

        return "admin/invoices";
    }

    @GetMapping("/report/export")
    public ResponseEntity<byte[]> exportReport() {

        byte[] report = analyticsService.exportPlatformReport();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=hireconnect-platform-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }
}