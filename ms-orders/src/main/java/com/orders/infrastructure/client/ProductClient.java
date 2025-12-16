package com.orders.infrastructure.client;

import com.orders.application.dto.OrderedProductDTO;
import com.orders.infrastructure.exception.ExternalServiceDownException;
import com.orders.infrastructure.exception.ResourceNotFoundException;
import com.orders.infrastructure.health.ExternalServiceChecker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@AllArgsConstructor
public class ProductClient {

    private final static String BASE_PRODUCT_SERVICE_URL = "http://localhost:8082";
    private final static String PRODUCT_SERVICE_API = BASE_PRODUCT_SERVICE_URL + "/api/v1/products";
    private final WebClient webClient;
    private final ExternalServiceChecker externalServiceChecker;


    public boolean productNotExist(Long productId) {
        return !Boolean.TRUE.equals(webClient.get()
                .uri(PRODUCT_SERVICE_API + "/{id}", productId)
                .retrieve()
                .toBodilessEntity()
                .map(res -> res.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block());
    }


    public OrderedProductDTO getProduct(Long productId) {
        if (!externalServiceChecker.checkService(BASE_PRODUCT_SERVICE_URL)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return webClient.get()
                .uri(PRODUCT_SERVICE_API + "/{id}", productId)
                .retrieve()
                .bodyToMono(OrderedProductDTO.class)
                .block();
    }

    public OrderedProductDTO updateStock(Long productId, Integer quantity) {
        if (!externalServiceChecker.checkService(BASE_PRODUCT_SERVICE_URL)) {
            throw new ExternalServiceDownException("product-service", "productId", productId.toString());
        }
        if (productNotExist(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        Map<String, Integer> body = Map.of("quantity", quantity);
        return webClient.patch()
                .uri(PRODUCT_SERVICE_API + "/{id}/stock", productId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OrderedProductDTO.class)
                .block();
    }

}
