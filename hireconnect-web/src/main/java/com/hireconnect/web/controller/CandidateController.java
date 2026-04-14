package com.hireconnect.web.controller;

import com.hireconnect.web.dto.RegisterRequest;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.NotificationService;
import com.hireconnect.web.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        model.addAttribute("pageTitle", "Candidate Dashboard");
        model.addAttribute("profile", profileService.getCandidateProfile(candidateId));
        model.addAttribute("recommendedJobs", jobService.getAllJobs());
        model.addAttribute("recentApplications",
                applicationService.getApplicationsByCandidate(candidateId));
        model.addAttribute("interviews",
                interviewService.getCandidateInterviews(candidateId));
        model.addAttribute("notifications",
                notificationService.getNotifications(candidateId));

        return "candidate/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        RegisterRequest request = new RegisterRequest();
        request.setRole("CANDIDATE");

        model.addAttribute("registerRequest", request);
        model.addAttribute("pageTitle", "Candidate Registration");

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result,
                           Model model) {

        request.setRole("CANDIDATE");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Password and confirm password do not match"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Candidate Registration");
            return "auth/register";
        }

        profileService.registerCandidate(request);

        return "redirect:/login?registered=true";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("profile", profileService.getCandidateProfile(candidateId));

        return "candidate/profile";
    }

    @GetMapping("/jobs")
    public String jobs(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String location,
                       Model model) {

        model.addAttribute("pageTitle", "Browse Jobs");
        model.addAttribute("jobs",
                (keyword != null || location != null)
                        ? jobService.searchJobs(keyword, location)
                        : jobService.getAllJobs());

        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);

        return "candidate/jobs";
    }

    @GetMapping("/jobs/{jobId}")
    public String viewJob(@PathVariable Long jobId,
                          Model model) {

        model.addAttribute("pageTitle", "Job Details");
        model.addAttribute("job", jobService.getJobById(jobId));

        return "candidate/job-details";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable Long jobId,
                              Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        applicationService.applyForJob(candidateId, jobId);

        return "redirect:/candidate/applications?success=Job application submitted";
    }

    @PostMapping("/jobs/{jobId}/bookmark")
    public String bookmarkJob(@PathVariable Long jobId,
                              Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        profileService.bookmarkJob(candidateId, jobId);

        return "redirect:/candidate/jobs?success=Job bookmarked";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model,
                                   Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        model.addAttribute("pageTitle", "My Applications");
        model.addAttribute("applications",
                applicationService.getApplicationsByCandidate(candidateId));

        return "candidate/applications";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model,
                                 Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        model.addAttribute("pageTitle", "My Interviews");
        model.addAttribute("interviews",
                interviewService.getCandidateInterviews(candidateId));

        return "candidate/interviews";
    }

    @GetMapping("/notifications")
    public String viewNotifications(Model model,
                                    Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        model.addAttribute("pageTitle", "Notifications");
        model.addAttribute("notifications",
                notificationService.getNotifications(candidateId));

        return "candidate/notifications";
    }

    @PostMapping("/notifications/{notificationId}/read")
    public String markNotificationAsRead(@PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return "redirect:/candidate/notifications";
    }

    @PostMapping("/wallet/add")
    public String addMoneyToWallet(@RequestParam Double amount,
                                   Authentication authentication) {

        UUID candidateId = getCandidateId(authentication);

        profileService.addMoneyToWallet(candidateId, amount);

        return "redirect:/candidate/profile?success=Wallet updated successfully";
    }

    private UUID getCandidateId(Authentication authentication) {
        return profileService.getCandidateIdByEmail(authentication.getName());
    }
}