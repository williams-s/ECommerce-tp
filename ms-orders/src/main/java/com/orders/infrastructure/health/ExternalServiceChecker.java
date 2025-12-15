package com.orders.infrastructure.health;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ExternalServiceChecker {

    private final WebClient webClient;

    public boolean checkService(String baseUrl) {
        try {
            webClient
                    .get()
                    .uri(baseUrl + "/actuator/health")
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(2));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
