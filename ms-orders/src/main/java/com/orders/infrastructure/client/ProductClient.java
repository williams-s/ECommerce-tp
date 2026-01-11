package com.orders.infrastructure.client;

import com.orders.application.dto.OrderedProductDTO;
import com.orders.infrastructure.exception.ExternalServiceDownException;
import com.orders.infrastructure.exception.InvalidJwtException;
import com.orders.infrastructure.exception.ResourceNotFoundException;
import com.orders.infrastructure.health.ExternalServiceChecker;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Objects;

@Component
public class ProductClient {

    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;
    private final String baseProductServiceUrl;
    private final String productServiceApi;

    public ProductClient(
            WebClient webClient,
            ExternalServiceChecker externalServiceChecker,
            @Value("${PRODUCT_SERVICE_URL}") String baseProductServiceUrl) {
        this.webClient = webClient;
        this.externalServiceChecker = externalServiceChecker;
        this.baseProductServiceUrl = Objects.requireNonNullElse(baseProductServiceUrl, "http://localhost:8082");
        this.productServiceApi = baseProductServiceUrl + "/api/v1/products";
    }

    private String getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }

    public boolean productNotExist(Long productId) {
        try {
            return !Boolean.TRUE.equals(webClient.get()
                    .uri(productServiceApi + "/{id}", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .toBodilessEntity()
                    .map(res -> res.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }

    public OrderedProductDTO getProduct(Long productId) {
        if (!externalServiceChecker.checkService(baseProductServiceUrl)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        try {
            return webClient.get()
                    .uri(productServiceApi + "/{id}", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(OrderedProductDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }

    public OrderedProductDTO updateStock(Long productId, Integer quantity) {
        if (!externalServiceChecker.checkService(baseProductServiceUrl)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        Map<String, Integer> body = Map.of("quantity", quantity);
        try {
            return webClient.patch()
                    .uri(productServiceApi + "/{id}/stock", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(OrderedProductDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }
}
