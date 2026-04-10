package com.hireconnect.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        for (GrantedAuthority authority : authentication.getAuthorities()) {

            String role = authority.getAuthority();

            if ("ROLE_RECRUITER".equals(role)) {
                response.sendRedirect("/recruiter/dashboard");
                return;
            }

            if ("ROLE_CANDIDATE".equals(role)) {
                response.sendRedirect("/candidate/home");
                return;
            }

            if ("ROLE_ADMIN".equals(role)) {
                response.sendRedirect("/admin/dashboard");
                return;
            }
        }

        response.sendRedirect("/");
    }
}