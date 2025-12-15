package com.products.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.products.domain.repository.ProductRepository;

/**
 * Health Indicator personnalisé pour vérifier les produits en stock bas.
 * Best practices :
 * - Implémente HealthIndicator pour les checks personnalisés
 * - Fournit des détails utiles pour le debugging
 * - Gère les exceptions proprement
 * - Utilisé par /actuator/health
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockHealthIndicator implements HealthIndicator {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;

    @Override
    public Health health() {
        try {
            long totalProducts = productRepository.count();
            long lowStockProducts = productRepository.countByStockLessThan(LOW_STOCK_THRESHOLD);

            log.debug(
                    "Health check product stock - Total products: {}, Low stock (< {}): {}",
                    totalProducts, LOW_STOCK_THRESHOLD, lowStockProducts
            );

            return Health.up()
                    .withDetail("service", "PRODUCT")
                    .withDetail("status", "Stock check OK")
                    .withDetail("totalProducts", totalProducts)
                    .withDetail("lowStockThreshold", LOW_STOCK_THRESHOLD)
                    .withDetail("lowStockProducts", lowStockProducts)
                    .build();

        } catch (Exception e) {
            log.error("Health check product stock failed", e);

            return Health.down()
                    .withDetail("service", "PRODUCT")
                    .withDetail("status", "Stock check failed")
                    .withDetail("lowStockThreshold", LOW_STOCK_THRESHOLD)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
