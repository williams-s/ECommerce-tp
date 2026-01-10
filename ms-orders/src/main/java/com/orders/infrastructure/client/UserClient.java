package com.orders.infrastructure.client;

import com.orders.infrastructure.exception.ExternalServiceDownException;
import com.orders.infrastructure.exception.InvalidJwtException;
import com.orders.infrastructure.health.ExternalServiceChecker;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@AllArgsConstructor
public class UserClient {

    private static final String BASE_USER_SERVICE_URL = "http://localhost:8081";
    private static final String USER_SERVICE_API = BASE_USER_SERVICE_URL + "/api/v1/users";

    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;

    private String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }

    public boolean isUserExist(Long userId) {
        if (!externalServiceChecker.checkService(BASE_USER_SERVICE_URL)) {
            throw new ExternalServiceDownException("user-service", "userId", userId.toString());
        }
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri(USER_SERVICE_API + "/{id}", userId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .toBodilessEntity()
                    .map(res -> res.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new InvalidJwtException("user-service");
            }
            throw e;
        }
    }

}
