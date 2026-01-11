package com.orders.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                        .authenticationEntryPoint((request, response, authException) -> {

                            int status;
                            String message;

                            if (authException.getCause() instanceof JwtException) {
                                status = HttpServletResponse.SC_FORBIDDEN;
                                message = "Token expired or invalid";
                            } else {
                                status = HttpServletResponse.SC_UNAUTHORIZED;
                                message = "Missing or invalid JWT token";
                            }

                            response.setStatus(status);
                            response.setContentType("application/json");

                            Map<String, Object> body = Map.of(
                                    "timestamp", Instant.now().toString(),
                                    "status", status,
                                    "error", status == 401 ? "UNAUTHORIZED" : "FORBIDDEN",
                                    "message", message,
                                    "path", request.getRequestURI()
                            );

                            objectMapper.writeValue(response.getOutputStream(), body);
                        })
                );

        return http.build();
    }
}
