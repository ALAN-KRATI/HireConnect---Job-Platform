package com.hireconnect.web.controller;

import com.hireconnect.web.dto.InterviewDto;
import com.hireconnect.web.dto.JobDto;
import com.hireconnect.web.service.AnalyticsService;
import com.hireconnect.web.service.ApplicationService;
import com.hireconnect.web.service.InterviewService;
import com.hireconnect.web.service.JobService;
import com.hireconnect.web.service.ProfileService;
import com.hireconnect.web.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/dashboard")
    public String recruiterDashboard(Model model) {
        Long recruiterId = 1L;

        model.addAttribute("jobs", jobService.getJobsByRecruiter(recruiterId));
        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));

        return "recruiter/dashboard";
    }

    @GetMapping("/jobs/post")
    public String postJobForm(Model model) {
        model.addAttribute("job", new JobDto());
        return "recruiter/post-job";
    }

    @PostMapping("/jobs/post")
    public String postJob(@ModelAttribute JobDto jobDto) {
        Long recruiterId = 1L;
        jobDto.setRecruiterId(recruiterId);

        jobService.createJob(jobDto);

        return "redirect:/recruiter/dashboard";
    }

    @GetMapping("/jobs/edit/{jobId}")
    public String editJob(@PathVariable Long jobId, Model model) {
        model.addAttribute("job", jobService.getJobById(jobId));
        return "recruiter/edit-job";
    }

    @PostMapping("/jobs/edit/{jobId}")
    public String updateJob(@PathVariable Long jobId,
                            @ModelAttribute JobDto jobDto) {

        jobService.updateJob(jobId, jobDto);
        return "redirect:/recruiter/dashboard";
    }

    @GetMapping("/applications")
    public String viewApplications(Model model) {
        Long recruiterId = 1L;

        model.addAttribute("applications",
                applicationService.getApplicationsForRecruiter(recruiterId));

        return "recruiter/applications";
    }

    @PostMapping("/applications/{applicationId}/shortlist")
    public String shortlistCandidate(@PathVariable Long applicationId) {
        applicationService.shortlistCandidate(applicationId);
        return "redirect:/recruiter/applications";
    }

    @PostMapping("/applications/{applicationId}/reject")
    public String rejectCandidate(@PathVariable Long applicationId) {
        applicationService.rejectCandidate(applicationId);
        return "redirect:/recruiter/applications";
    }

    @GetMapping("/interview/schedule/{applicationId}")
    public String scheduleInterviewForm(@PathVariable Long applicationId,
                                        Model model) {

        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setApplicationId(applicationId);

        model.addAttribute("interview", interviewDto);
        return "recruiter/schedule-interview";
    }

    @PostMapping("/interview/schedule")
    public String scheduleInterview(@ModelAttribute InterviewDto interviewDto) {
        interviewService.scheduleInterview(interviewDto);
        return "redirect:/recruiter/applications";
    }

    @GetMapping("/analytics")
    public String viewAnalytics(Model model) {
        Long recruiterId = 1L;

        model.addAttribute("analytics",
                analyticsService.getRecruiterAnalytics(recruiterId));

        return "recruiter/analytics";
    }

    @GetMapping("/subscription")
    public String managePlan(Model model) {
        Long recruiterId = 1L;

        model.addAttribute("subscription",
                subscriptionService.getCurrentPlan(recruiterId));

        return "recruiter/subscription";
    }

    @GetMapping("/invoices")
    public String viewInvoices(Model model) {
        Long recruiterId = 1L;

        model.addAttribute("invoices",
                subscriptionService.getInvoices(recruiterId));

        return "recruiter/invoices";
    }
}