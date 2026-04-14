package com.hireconnect.gateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth",
            "/oauth2",
            "/login",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );
    
    // Paths that can be accessed with OR without authentication
    private static final List<String> OPTIONAL_AUTH_PATHS = List.of(
            "/jobs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Allow public paths without any auth
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange);
        }
        
        // Check if path allows optional authentication
        boolean isOptionalAuth = OPTIONAL_AUTH_PATHS.stream().anyMatch(path::startsWith);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // If no auth header and path is optional auth, allow through
        if ((authHeader == null || !authHeader.startsWith("Bearer ")) && isOptionalAuth) {
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT FILTER] Missing or invalid Authorization header for path: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("[JWT FILTER] Token validation failed for path: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractClaims(token);

        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        String userId = claims.get("userId", String.class);

        System.out.println("[JWT FILTER] PATH = " + path);
        System.out.println("[JWT FILTER] EMAIL = " + email);
        System.out.println("[JWT FILTER] ROLE = " + role);
        System.out.println("[JWT FILTER] USER_ID = " + userId);

        // Admin can access everything
        if ("ADMIN".equals(role)) {
            return chain.filter(
                    exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-Authenticated-User", email)
                                    .header("X-Authenticated-Role", role)
                                    .header("X-User-Id", userId != null ? userId : "")
                                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                                    .build())
                            .build()
            );
        }

        // Candidate-only routes (block non-candidates from candidate-specific endpoints)
        boolean isCandidateOnlyPath = 
            path.startsWith("/profiles/me") ||
            path.startsWith("/profiles/candidates") ||
            path.startsWith("/applications/candidate") ||
            path.startsWith("/interviews/candidate");
        
        if (isCandidateOnlyPath && !"CANDIDATE".equals(role)) {
            System.out.println("[JWT FILTER] 403 - Non-candidate accessing candidate-only path: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        
        // Recruiter-only routes (block non-recruiters from recruiter-specific endpoints)
        boolean isRecruiterOnlyPath = 
            path.startsWith("/profiles/recruiters") ||
            path.startsWith("/applications/recruiter") ||
            path.startsWith("/interviews/recruiter") ||
            path.startsWith("/subscriptions") ||
            path.startsWith("/analytics");
        
        if (isRecruiterOnlyPath && !"RECRUITER".equals(role)) {
            System.out.println("[JWT FILTER] 403 - Non-recruiter accessing recruiter-only path: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // Recruiter-only routes
        if ((path.startsWith("/subscriptions")
                || path.startsWith("/analytics"))
                && !"RECRUITER".equals(role)) {

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-Authenticated-User", email)
                                .header("X-Authenticated-Role", role)
                                .header("X-User-Id", userId != null ? userId : "")
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build())
                        .build()
        );
    }
}