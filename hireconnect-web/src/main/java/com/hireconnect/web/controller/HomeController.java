package com.hireconnect.web.controller;

import com.hireconnect.web.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final JobService jobService;

    public HomeController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/")
    public String landingPage(Model model) {

        model.addAttribute("pageTitle", "HireConnect - Find Your Dream Job");
        model.addAttribute("featuredJobs", jobService.getAllJobs()
                .stream()
                .limit(6)
                .toList());

        return "index";
    }

    @GetMapping("/jobs")
    public String browseJobs(Model model) {

        model.addAttribute("pageTitle", "Browse Jobs");
        model.addAttribute("jobs", jobService.getAllJobs());

        return "jobs";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {

        model.addAttribute("pageTitle", "Access Denied");

        return "common/access-denied";
    }

    @GetMapping("/error")
    public String errorPage(Model model) {

        model.addAttribute("pageTitle", "Something Went Wrong");

        return "common/error";
    }
}