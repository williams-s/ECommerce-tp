package com.orders.application.mapper;

import com.orders.application.dto.OrderItemRequestDTO;
import com.orders.application.dto.OrderRequestDTO;
import com.orders.application.dto.OrderResponseDTO;
import com.orders.application.dto.OrderedProductDTO;
import com.orders.domain.entity.Order;
import com.orders.domain.entity.OrderItem;
import com.orders.infrastructure.client.ProductClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    private ProductClient productClient;
    private OrderItemMapper orderItemMapper;

    public Order toEntity(OrderRequestDTO orderRequestDTO) {
        List<OrderItemRequestDTO> orderItems = orderRequestDTO.getOrderItems();
        List<BigDecimal> productsPrice = orderItems.stream().map(orderItem -> productClient.getProduct(orderItem.getProductId()).getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))).toList();
        BigDecimal totalAmount = new BigDecimal(0);
        for (BigDecimal price : productsPrice) {
            totalAmount = totalAmount.add(price);
        }
        Order order = Order.builder()
                .userId(orderRequestDTO.getUserId())
                .status(orderRequestDTO.getStatus())
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .shippingAddress(orderRequestDTO.getShippingAddress())
                .build();

        List<OrderItem> items = orderRequestDTO.getOrderItems().stream()
                .map(orderItemMapper::toEntity)
                .toList();

        for (OrderItem item : items) {
            item.setOrder(order);
        }

        order.setOrderItems(items);
        return order;
    }

    public OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(order.getOrderItems().stream().map(orderItemMapper::toDTO).collect(Collectors.toList()))
                .build();
    }

}
