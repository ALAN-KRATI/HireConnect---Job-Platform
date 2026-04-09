package com.hireconnect.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers(HttpMethod.GET,
                    "/applications/job/**",
                    "/applications/platform/**"
                ).permitAll()

                .requestMatchers(HttpMethod.POST,
                    "/applications"
                ).hasRole("CANDIDATE")

                .requestMatchers(HttpMethod.GET,
                    "/applications/candidate/**"
                ).hasRole("CANDIDATE")

                .requestMatchers(HttpMethod.GET,
                    "/applications/{applicationId}"
                ).authenticated()

                .requestMatchers(HttpMethod.PUT,
                    "/applications/*/withdraw"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    "/applications/*/shortlist",
                    "/applications/*/reject",
                    "/applications/*/advance",
                    "/applications/*/status",
                    "/applications/recruiter/**"
                ).hasRole("RECRUITER")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}