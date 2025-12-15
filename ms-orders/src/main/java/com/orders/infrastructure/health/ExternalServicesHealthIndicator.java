package com.orders.infrastructure.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final ExternalServiceChecker externalServiceChecker;

    @Override
    public Health health() {
        boolean userUp = externalServiceChecker.checkService("http://localhost:8081");
        boolean productUp = externalServiceChecker.checkService("http://localhost:8082");

        if (userUp && productUp) {
            return Health.up()
                    .withDetail("user-service", "UP")
                    .withDetail("product-service", "UP")
                    .build();
        }

        return Health.down()
                .withDetail("user-service", userUp ? "UP" : "DOWN")
                .withDetail("product-service", productUp ? "UP" : "DOWN")
                .build();
    }
}
