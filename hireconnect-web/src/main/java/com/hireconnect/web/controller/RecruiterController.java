package com.hireconnect.web.controller;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.enums.EmploymentType;
import com.hireconnect.web.enums.JobStatus;
import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;

import java.util.UUID;
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
                                     org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Recruiter Dashboard");
        model.addAttribute("profile", profileService.getRecruiterProfile(recruiterId));
        model.addAttribute("jobs", jobService.getJobsByRecruiter(recruiterId));
        model.addAttribute("applications",
                applicationService.getApplicationsForRecruiter(recruiterId));
        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));
        model.addAttribute("subscription",
                subscriptionService.getCurrentPlan(recruiterId));

        return "recruiter/dashboard";
    }

    @GetMapping("/jobs")
    public String recruiterJobs(Model model,
                                org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "My Jobs");
        model.addAttribute("jobs", jobService.getJobsByRecruiter(recruiterId));

        return "recruiter/jobs";
    }

    @GetMapping("/jobs/new")
    public String postJobForm(Model model) {

        JobDto job = new JobDto();
        job.setStatus(JobStatus.OPEN);

        model.addAttribute("pageTitle", "Post New Job");
        model.addAttribute("job", job);
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("jobStatuses", JobStatus.values());

        return "recruiter/post-job";
    }

    @PostMapping("/jobs/new")
    public String postJob(@Valid @ModelAttribute("job") JobDto jobDto,
                          org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        jobDto.setRecruiterId(recruiterId);

        jobService.createJob(jobDto);

        return "redirect:/recruiter/jobs?success=Job created successfully";
    }

    @GetMapping("/jobs/{jobId}/edit")
    public String editJob(@PathVariable Long jobId,
                          Model model) {

        model.addAttribute("pageTitle", "Edit Job");
        model.addAttribute("job", jobService.getJobById(jobId));
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("jobStatuses", JobStatus.values());

        return "recruiter/edit-job";
    }

    @PostMapping("/jobs/{jobId}/edit")
    public String updateJob(@PathVariable Long jobId,
                            @ModelAttribute("job") JobDto jobDto,
                            org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        jobDto.setRecruiterId(recruiterId);

        jobService.updateJob(jobId, jobDto);

        return "redirect:/recruiter/jobs?success=Job updated successfully";
    }

    @PostMapping("/jobs/{jobId}/pause")
    public String pauseJob(@PathVariable Long jobId) {

        jobService.pauseJob(jobId);

        return "redirect:/recruiter/jobs?success=Job paused successfully";
    }

    @PostMapping("/jobs/{jobId}/close")
    public String closeJob(@PathVariable Long jobId) {

        jobService.closeJob(jobId);

        return "redirect:/recruiter/jobs?success=Job closed successfully";
    }

    @PostMapping("/jobs/{jobId}/delete")
    public String deleteJob(@PathVariable Long jobId) {

        jobService.deleteJob(jobId);

        return "redirect:/recruiter/jobs?success=Job deleted successfully";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model,
                                   org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Applications");
        model.addAttribute("applications",
                applicationService.getApplicationsForRecruiter(recruiterId));

        return "recruiter/applications";
    }

    @PostMapping("/applications/{applicationId}/shortlist")
    public String shortlistCandidate(@PathVariable Long applicationId) {

        applicationService.shortlistCandidate(applicationId);

        return "redirect:/recruiter/applications?success=Candidate shortlisted";
    }

    @PostMapping("/applications/{applicationId}/reject")
    public String rejectCandidate(@PathVariable Long applicationId) {

        applicationService.rejectCandidate(applicationId);

        return "redirect:/recruiter/applications?success=Candidate rejected";
    }

    @PostMapping("/applications/{applicationId}/offer")
    public String offerCandidate(@PathVariable Long applicationId) {

        applicationService.offerCandidate(applicationId);

        return "redirect:/recruiter/applications?success=Offer sent";
    }

    @GetMapping("/interviews/schedule/{applicationId}")
    public String scheduleInterviewForm(@PathVariable Long applicationId,
                                        Model model) {

        InterviewDto interview = new InterviewDto();
        interview.setApplicationId(applicationId);

        model.addAttribute("pageTitle", "Schedule Interview");
        model.addAttribute("interview", interview);

        return "recruiter/schedule-interview";
    }

    @PostMapping("/interviews/schedule")
    public String scheduleInterview(@ModelAttribute("interview") InterviewDto interviewDto,
                                    org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);
        interviewDto.setRecruiterId(recruiterId);

        interviewService.scheduleInterview(interviewDto);

        return "redirect:/recruiter/interviews?success=Interview scheduled";
    }

    @GetMapping("/interviews")
    public String viewInterviews(Model model,
                                 org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Interviews");
        model.addAttribute("interviews",
                interviewService.getRecruiterInterviews(recruiterId));

        return "recruiter/interviews";
    }

    @GetMapping("/analytics")
    public String viewAnalytics(Model model,
                                org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Recruiter Analytics");
        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));

        return "recruiter/analytics";
    }

    @GetMapping("/subscription")
    public String managePlan(Model model,
                             org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Subscription");
        model.addAttribute("subscription",
                subscriptionService.getCurrentPlan(recruiterId));
        model.addAttribute("plans",
                subscriptionService.getAvailablePlans());

        return "recruiter/subscription";
    }

    @PostMapping("/subscription/upgrade")
    public String upgradeSubscription(@RequestParam String plan,
                                      org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        subscriptionService.upgradePlan(recruiterId, plan);

        return "redirect:/recruiter/subscription?success=Plan upgraded successfully";
    }

    @PostMapping("/subscription/cancel")
    public String cancelSubscription(org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        subscriptionService.cancelPlan(recruiterId);

        return "redirect:/recruiter/subscription?success=Subscription cancelled";
    }

    @GetMapping("/invoices")
    public String viewInvoices(Model model,
                               org.springframework.security.core.Authentication authentication) {

        UUID recruiterId = getRecruiterId(authentication);

        model.addAttribute("pageTitle", "Invoices");
        model.addAttribute("invoices",
                subscriptionService.getInvoices(recruiterId));

        return "recruiter/invoices";
    }

    private UUID getRecruiterId(org.springframework.security.core.Authentication authentication) {
        return profileService.getRecruiterIdByEmail(authentication.getName());
    }
}