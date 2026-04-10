package com.hireconnect.web.controller;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final ProfileService profileService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;
    private final AnalyticsService analyticsService;
    private final SubscriptionService subscriptionService;

    public RecruiterController(ProfileService profileService,
                               JobService jobService,
                               ApplicationService applicationService,
                               InterviewService interviewService,
                               AnalyticsService analyticsService,
                               SubscriptionService subscriptionService) {
        this.profileService = profileService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.interviewService = interviewService;
        this.analyticsService = analyticsService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/dashboard")
    public String recruiterDashboard(Model model,
                                     Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("profile",
                profileService.getRecruiterProfile(recruiterId));

        model.addAttribute("jobs",
                jobService.getJobsByRecruiter(recruiterId));

        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));

        model.addAttribute("subscription",
                subscriptionService.getCurrentPlan(recruiterId));

        return "recruiter/dashboard";
    }

    @GetMapping("/jobs")
    public String recruiterJobs(Model model,
                                Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("jobs",
                jobService.getJobsByRecruiter(recruiterId));

        return "recruiter/jobs";
    }

    @GetMapping("/jobs/post")
    public String postJobForm(Model model) {

        model.addAttribute("job", new JobDto());

        return "recruiter/post-job";
    }

    @PostMapping("/jobs/post")
    public String postJob(@ModelAttribute("job") JobDto jobDto,
                          Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        jobDto.setRecruiterId(recruiterId);

        jobService.createJob(jobDto);

        return "redirect:/recruiter/jobs?success=created";
    }

    @GetMapping("/jobs/edit/{jobId}")
    public String editJob(@PathVariable("jobId") Long jobId,
                          Model model) {

        model.addAttribute("job",
                jobService.getJobById(jobId));

        return "recruiter/edit-job";
    }

    @PostMapping("/jobs/edit/{jobId}")
    public String updateJob(@PathVariable("jobId") Long jobId,
                            @ModelAttribute("job") JobDto jobDto,
                            Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        jobDto.setRecruiterId(recruiterId);

        jobService.updateJob(jobId, jobDto);

        return "redirect:/recruiter/jobs?success=updated";
    }

    @PostMapping("/jobs/{jobId}/pause")
    public String pauseJob(@PathVariable("jobId") Long jobId) {

        jobService.pauseJob(jobId);

        return "redirect:/recruiter/jobs?success=paused";
    }

    @PostMapping("/jobs/{jobId}/close")
    public String closeJob(@PathVariable("jobId") Long jobId) {

        jobService.closeJob(jobId);

        return "redirect:/recruiter/jobs?success=closed";
    }

    @PostMapping("/jobs/{jobId}/delete")
    public String deleteJob(@PathVariable("jobId") Long jobId) {

        jobService.deleteJob(jobId);

        return "redirect:/recruiter/jobs?success=deleted";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model,
                                   Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("applications",
                applicationService.getApplicationsForRecruiter(recruiterId));

        return "recruiter/applications";
    }

    @PostMapping("/applications/{applicationId}/shortlist")
    public String shortlistCandidate(@PathVariable("applicationId") Long applicationId) {

        applicationService.shortlistCandidate(applicationId);

        return "redirect:/recruiter/applications?success=shortlisted";
    }

    @PostMapping("/applications/{applicationId}/reject")
    public String rejectCandidate(@PathVariable("applicationId") Long applicationId) {

        applicationService.rejectCandidate(applicationId);

        return "redirect:/recruiter/applications?success=rejected";
    }

    @PostMapping("/applications/{applicationId}/offer")
    public String offerCandidate(@PathVariable("applicationId") Long applicationId) {

        applicationService.offerCandidate(applicationId);

        return "redirect:/recruiter/applications?success=offered";
    }

    @GetMapping("/interview/schedule/{applicationId}")
    public String scheduleInterviewForm(@PathVariable("applicationId") Long applicationId,
                                        Model model) {

        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setApplicationId(applicationId);

        model.addAttribute("interview", interviewDto);

        return "recruiter/schedule-interview";
    }

    @PostMapping("/interview/schedule")
    public String scheduleInterview(@ModelAttribute("interview") InterviewDto interviewDto) {

        interviewService.scheduleInterview(interviewDto);

        return "redirect:/recruiter/applications?success=interviewScheduled";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model,
                                 Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("interviews",
                interviewService.getRecruiterInterviews(recruiterId));

        return "recruiter/interviews";
    }

    @GetMapping("/analytics")
    public String viewAnalytics(Model model,
                                Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));

        return "recruiter/analytics";
    }

    @GetMapping("/subscription")
    public String managePlan(Model model,
                             Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("subscription",
                subscriptionService.getCurrentPlan(recruiterId));

        model.addAttribute("plans",
                subscriptionService.getAvailablePlans());

        return "recruiter/subscription";
    }

    @PostMapping("/subscription/upgrade")
    public String upgradeSubscription(@RequestParam("plan") String plan,
                                      Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        subscriptionService.upgradePlan(recruiterId, plan);

        return "redirect:/recruiter/subscription?success=upgraded";
    }

    @GetMapping("/invoices")
    public String viewInvoices(Model model,
                               Authentication authentication) {

        String email = authentication.getName();
        Long recruiterId = profileService.getRecruiterIdByEmail(email);

        model.addAttribute("invoices",
                subscriptionService.getInvoices(recruiterId));

        return "recruiter/invoices";
    }
}