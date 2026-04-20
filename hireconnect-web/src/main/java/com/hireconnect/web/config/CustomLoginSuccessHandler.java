package com.hireconnect.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String targetUrl = "/";

        if (hasRole(authorities, "ROLE_ADMIN")) {
            targetUrl = "/admin/dashboard";
        } else if (hasRole(authorities, "ROLE_RECRUITER")) {
            targetUrl = "/recruiter/dashboard";
        } else if (hasRole(authorities, "ROLE_CANDIDATE")) {
            targetUrl = "/candidate/dashboard";
        }

        setAlwaysUseDefaultTargetUrl(true);
        setDefaultTargetUrl(targetUrl);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}