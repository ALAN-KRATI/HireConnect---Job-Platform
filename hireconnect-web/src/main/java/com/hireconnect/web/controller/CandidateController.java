package com.hireconnect.web.controller;

import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.NotificationService;
import com.hireconnect.web.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "candidate/home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request) {
        profileService.registerCandidate(request);
        return "redirect:/login";
    }


    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Long candidateId = 1L; // later take from logged-in user
        model.addAttribute("profile", profileService.getCandidateProfile(candidateId));
        return "candidate/profile";
    }

    @GetMapping("/jobs")
    public String searchJobs(Model model) {
        model.addAttribute("jobs", jobService.getAllJobs());
        return "candidate/jobs";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable Long jobId) {
        Long candidateId = 1L;
        applicationService.applyForJob(candidateId, jobId);
        return "redirect:/candidate/applications";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        Long candidateId = 1L;
        model.addAttribute("applications",
                applicationService.getApplicationsByCandidate(candidateId));
        return "candidate/applications";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model) {
        Long candidateId = 1L;
        model.addAttribute("interviews",
                interviewService.getCandidateInterviews(candidateId));
        return "candidate/interviews";
    }

    @GetMapping("/notifications")
    public String viewNotifications(Model model) {
        Long candidateId = 1L;
        model.addAttribute("notifications",
                notificationService.getNotifications(candidateId));
        return "candidate/notifications";
    }

    @PostMapping("/jobs/{jobId}/bookmark")
    public String bookmarkJob(@PathVariable Long jobId) {
        Long candidateId = 1L;
        profileService.bookmarkJob(candidateId, jobId);
        return "redirect:/candidate/jobs";
    }

    @PostMapping("/wallet/add")
    public String addMoneyToWallet(@RequestParam Double amount) {
        Long candidateId = 1L;
        profileService.addMoneyToWallet(candidateId, amount);
        return "redirect:/candidate/profile";
    }
}