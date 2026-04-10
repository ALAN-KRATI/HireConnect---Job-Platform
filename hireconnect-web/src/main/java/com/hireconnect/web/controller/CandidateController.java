package com.hireconnect.web.controller;

import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.NotificationService;
import com.hireconnect.web.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final ProfileService profileService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;
    private final NotificationService notificationService;

    public CandidateController(ProfileService profileService,
                               JobService jobService,
                               ApplicationService applicationService,
                               InterviewService interviewService,
                               NotificationService notificationService) {
        this.profileService = profileService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.interviewService = interviewService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        model.addAttribute("profile",
                profileService.getCandidateProfile(candidateId));

        model.addAttribute("jobs",
                jobService.getAllJobs());

        model.addAttribute("recentApplications",
                applicationService.getApplicationsByCandidate(candidateId));

        model.addAttribute("notifications",
                notificationService.getNotifications(candidateId));

        return "candidate/home";
    }

    @GetMapping("/register")
    public String register(Model model) {

        RegisterRequest request = new RegisterRequest();
        request.setRole("CANDIDATE");

        model.addAttribute("registerRequest", request);

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerRequest") RegisterRequest request) {

        request.setRole("CANDIDATE");
        profileService.registerCandidate(request);

        return "redirect:/login?registered=true";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        model.addAttribute("profile",
                profileService.getCandidateProfile(candidateId));

        return "candidate/profile";
    }

    @GetMapping("/jobs")
    public String searchJobs(Model model) {

        model.addAttribute("jobs",
                jobService.getAllJobs());

        return "candidate/jobs";
    }

    @GetMapping("/jobs/{jobId}")
    public String viewJob(@PathVariable("jobId") Long jobId,
                          Model model) {

        model.addAttribute("job",
                jobService.getJobById(jobId));

        return "candidate/job-details";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable("jobId") Long jobId,
                              Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        applicationService.applyForJob(candidateId, jobId);

        return "redirect:/candidate/applications?success=applied";
    }

    @PostMapping("/jobs/{jobId}/bookmark")
    public String bookmarkJob(@PathVariable("jobId") Long jobId,
                              Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        profileService.bookmarkJob(candidateId, jobId);

        return "redirect:/candidate/jobs?success=bookmarked";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model,
                                   Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        model.addAttribute("applications",
                applicationService.getApplicationsByCandidate(candidateId));

        return "candidate/applications";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model,
                                 Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        model.addAttribute("interviews",
                interviewService.getCandidateInterviews(candidateId));

        return "candidate/interviews";
    }

    @GetMapping("/notifications")
    public String viewNotifications(Model model,
                                    Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        model.addAttribute("notifications",
                notificationService.getNotifications(candidateId));

        return "candidate/notifications";
    }

    @PostMapping("/notifications/{notificationId}/read")
    public String markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {

        notificationService.markAsRead(notificationId);

        return "redirect:/candidate/notifications";
    }

    @PostMapping("/wallet/add")
    public String addMoneyToWallet(@RequestParam("amount") Double amount,
                                   Authentication authentication) {

        String email = authentication.getName();
        Long candidateId = profileService.getCandidateIdByEmail(email);

        profileService.addMoneyToWallet(candidateId, amount);

        return "redirect:/candidate/profile?success=walletUpdated";
    }
}