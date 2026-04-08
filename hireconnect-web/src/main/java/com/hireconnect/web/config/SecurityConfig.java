package com.hireconnect.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/perform-login",
                                "/candidate/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/access-denied",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/candidate/**")
                        .hasAnyRole("CANDIDATE", "ADMIN")

                        .requestMatchers("/recruiter/**")
                        .hasAnyRole("RECRUITER", "ADMIN")

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform-login")
                        .defaultSuccessUrl("/candidate/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/candidate/home", true)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}