package com.orders.infrastructure.client;

import com.orders.infrastructure.exception.ExternalServiceDownException;
import com.orders.infrastructure.exception.InvalidJwtException;
import com.orders.infrastructure.health.ExternalServiceChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Component
@Slf4j
public class UserClient {

    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;
    private final String baseUserServiceUrl;
    private final String userServiceApi;

    public UserClient(
            WebClient webClient,
            ExternalServiceChecker externalServiceChecker,
            @Value("${USER_SERVICE_URL}") String baseUserServiceUrl
    ) {
        this.webClient = webClient;
        this.externalServiceChecker = externalServiceChecker;
        this.baseUserServiceUrl = Objects.requireNonNullElse(baseUserServiceUrl, "http://localhost:8081");
        this.userServiceApi = baseUserServiceUrl + "/api/v1/users";
    }

    private String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }

    public boolean isUserExist(Long userId) {
        if (!externalServiceChecker.checkService(baseUserServiceUrl)) {
            throw new ExternalServiceDownException("user-service", "userId", userId.toString());
        }
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri(userServiceApi + "/{id}", userId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .toBodilessEntity()
                    .map(res -> res.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new InvalidJwtException("user-service");
            }
            throw e;
        }
    }
}
