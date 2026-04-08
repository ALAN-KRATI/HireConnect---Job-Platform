package com.hireconnect.web.controller;

import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private JobService jobService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("users", profileService.getAllUsers());
        model.addAttribute("jobs", jobService.getAllJobs());
        model.addAttribute("analytics", analyticsService.getPlatformAnalytics());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", profileService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/jobs")
    public String viewAllJobs(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "admin/jobs";
    }

    @PostMapping("/users/{userId}/suspend")
    public String suspendUser(@PathVariable Long userId) {
        profileService.suspendUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/analytics")
    public String viewPlatformAnalytics(Model model) {
        model.addAttribute("analytics", analyticsService.getPlatformAnalytics());
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
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=hireconnect-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }
}