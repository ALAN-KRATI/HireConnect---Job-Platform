package com.hireconnect.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/")
                .setViewName("redirect:/login");

        registry.addViewController("/login")
                .setViewName("auth/login");

        registry.addViewController("/register")
                .setViewName("auth/register");

        registry.addViewController("/candidate/dashboard")
                .setViewName("candidate/dashboard");

        registry.addViewController("/recruiter/dashboard")
                .setViewName("recruiter/dashboard");

        registry.addViewController("/admin/dashboard")
                .setViewName("admin/dashboard");

        registry.addViewController("/access-denied")
                .setViewName("common/access-denied");

        registry.addViewController("/error")
                .setViewName("common/error");
    }
}