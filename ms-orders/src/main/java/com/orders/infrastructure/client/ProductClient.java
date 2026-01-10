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

import java.util.Map;

@Component
@AllArgsConstructor
public class ProductClient {

    private final static String BASE_PRODUCT_SERVICE_URL = "http://localhost:8082";
    private final static String PRODUCT_SERVICE_API = BASE_PRODUCT_SERVICE_URL + "/api/v1/products";
    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;


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
                    .uri(PRODUCT_SERVICE_API + "/{id}", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .toBodilessEntity()
                    .map(res -> res.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }


    public OrderedProductDTO getProduct(Long productId) {
        if (!externalServiceChecker.checkService(BASE_PRODUCT_SERVICE_URL)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        try {
            return webClient.get()
                    .uri(PRODUCT_SERVICE_API + "/{id}", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .retrieve()
                    .bodyToMono(OrderedProductDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }

    public OrderedProductDTO updateStock(Long productId, Integer quantity) {
        if (!externalServiceChecker.checkService(BASE_PRODUCT_SERVICE_URL)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        Map<String, Integer> body = Map.of("quantity", quantity);
        try {
            return webClient.patch()
                    .uri(PRODUCT_SERVICE_API + "/{id}/stock", productId)
                    .header("Authorization", "Bearer " + getToken())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(OrderedProductDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new InvalidJwtException("product-service");
            }
            throw e;
        }
    }
}
