package com.hireconnect.web.controller;

import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        model.addAttribute("totalUsers",
                profileService.getAllUsers().size());

        model.addAttribute("totalJobs",
                jobService.getAllJobs().size());

        model.addAttribute("users",
                profileService.getAllUsers());

        model.addAttribute("jobs",
                jobService.getAllJobs());

        model.addAttribute("analytics",
                analyticsService.getPlatformAnalytics());

        model.addAttribute("subscriptions",
                subscriptionService.getAllSubscriptions());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {

        model.addAttribute("users",
                profileService.getAllUsers());

        return "admin/users";
    }

    @PostMapping("/users/{userId}/suspend")
    public String suspendUser(@PathVariable("userId") Long userId) {

        profileService.suspendUser(userId);

        return "redirect:/admin/users?success=userSuspended";
    }

    @PostMapping("/users/{userId}/activate")
    public String activateUser(@PathVariable("userId") Long userId) {

        profileService.activateUser(userId);

        return "redirect:/admin/users?success=userActivated";
    }

    @GetMapping("/jobs")
    public String viewAllJobs(Model model) {

        model.addAttribute("jobs",
                jobService.getAllJobs());

        return "admin/jobs";
    }

    @PostMapping("/jobs/{jobId}/delete")
    public String deleteJob(@PathVariable("jobId") Long jobId) {

        jobService.deleteJob(jobId);

        return "redirect:/admin/jobs?success=jobDeleted";
    }

    @GetMapping("/analytics")
    public String viewPlatformAnalytics(Model model) {

        model.addAttribute("analytics",
                analyticsService.getPlatformAnalytics());

        return "admin/analytics";
    }

    @GetMapping("/subscriptions")
    public String manageSubscriptions(Model model) {

        model.addAttribute("subscriptions",
                subscriptionService.getAllSubscriptions());

        return "admin/subscriptions";
    }

    @GetMapping("/invoices")
    public String viewAllInvoices(Model model) {

        model.addAttribute("invoices",
                subscriptionService.getAllInvoices());

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