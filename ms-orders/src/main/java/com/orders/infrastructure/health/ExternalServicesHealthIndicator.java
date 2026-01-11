package com.orders.infrastructure.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Objects;

@Component
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final ExternalServiceChecker externalServiceChecker;
    private final String baseProductServiceUrl;
    private final String baseUserServiceUrl;

    public ExternalServicesHealthIndicator(ExternalServiceChecker externalServiceChecker,@Value("${PRODUCT_SERVICE_URL}") String baseProductServiceUrl,@Value("${USER_SERVICE_URL}") String baseUserServiceUrl) {
        this.externalServiceChecker = externalServiceChecker;
        this.baseProductServiceUrl = Objects.requireNonNullElse(baseProductServiceUrl, "http://localhost:8082");
        this.baseUserServiceUrl = Objects.requireNonNullElse(baseUserServiceUrl, "http://localhost:8081");
    }
    @Override
    public Health health() {
        boolean userUp = externalServiceChecker.checkService(baseUserServiceUrl);
        boolean productUp = externalServiceChecker.checkService(baseProductServiceUrl);

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
