package com.orders.application.mapper;

import com.orders.application.dto.OrderItemRequestDTO;
import com.orders.application.dto.OrderItemResponseDTO;
import com.orders.application.dto.OrderedProductDTO;
import com.orders.domain.entity.OrderItem;
import com.orders.infrastructure.client.ProductClient;
import com.orders.infrastructure.exception.ProductOutOfStockException;
import com.orders.infrastructure.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@AllArgsConstructor
public class OrderItemMapper {


    private final ProductClient productClient;


    public OrderItem toEntity(OrderItemRequestDTO orderItemRequestDTO) {
        OrderedProductDTO orderedProductDTO = productClient.getProduct(orderItemRequestDTO.getProductId());
        if (orderedProductDTO == null) {
            throw new ResourceNotFoundException("Product", "id", orderItemRequestDTO.getProductId());
        }
        if (orderedProductDTO.getStock() < orderItemRequestDTO.getQuantity()) {
            throw new ProductOutOfStockException(orderedProductDTO.getStock(), orderItemRequestDTO.getQuantity());
        }
        OrderedProductDTO updatedProduct = productClient.updateStock(orderItemRequestDTO.getProductId(), orderItemRequestDTO.getQuantity() * -1);
        if (updatedProduct == null) {
            throw new ResourceNotFoundException("Updated Product", "id", orderItemRequestDTO.getProductId());
        }

        log.info("Name of product: {}", orderedProductDTO.getName());
        return OrderItem.builder()
                .productId(orderItemRequestDTO.getProductId())
                .quantity(orderItemRequestDTO.getQuantity())
                .productName(orderedProductDTO.getName())
                .unitPrice(orderedProductDTO.getPrice())
                .subtotal(orderedProductDTO.getPrice().multiply(BigDecimal.valueOf(orderItemRequestDTO.getQuantity())))
                .build();
    }

    public OrderItemResponseDTO toDTO(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .productName(orderItem.getProductName())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

}
