package com.orders.infrastructure.client;

import com.orders.infrastructure.exception.ExternalServiceDownException;
import com.orders.infrastructure.health.ExternalServiceChecker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class UserClient {

    private static final String BASE_USER_SERVICE_URL = "http://localhost:8081";
    private static final String USER_SERVICE_API = BASE_USER_SERVICE_URL + "/api/v1/users";

    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;

    public boolean isUserExist(Long userId) {
        if (!externalServiceChecker.checkService(BASE_USER_SERVICE_URL)) {
            throw new ExternalServiceDownException("user-service", "userId", userId.toString());
        }
        return Boolean.TRUE.equals(webClient.get()
                .uri(USER_SERVICE_API + "/{id}", userId)
                .retrieve()
                .toBodilessEntity()
                .map(res -> res.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block());
    }

}
