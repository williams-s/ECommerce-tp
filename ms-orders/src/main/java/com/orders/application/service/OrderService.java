package com.orders.application.service;

import com.orders.application.dto.OrderRequestDTO;
import com.orders.application.dto.OrderResponseDTO;
import com.orders.application.mapper.OrderMapper;
import com.orders.domain.entity.Order;
import com.orders.domain.enums.OrderStatus;
import com.orders.domain.repository.OrderRepository;
import com.orders.infrastructure.client.UserClient;
import com.orders.infrastructure.exception.CommandCancelleOrDeliveredException;
import com.orders.infrastructure.exception.ResourceNotFoundException;
import com.orders.infrastructure.metrics.OrderMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderMetrics orderMetrics;
    private final UserClient userClient;

    private boolean userNotFound(Long userId) {
        return !userClient.isUserExist(userId);
    }

    public List<OrderResponseDTO> getOrders() {
        log.debug("Récupération de toutes les commandes");
        List<Order> orders = orderRepository.findAll();
        log.info("Nombre de commandes recuperées: {}", orders.size());
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    public OrderResponseDTO getOrderById(Long id) {
        if (userNotFound(id)) throw new ResourceNotFoundException("User", "id", id);
        log.debug("Récupération de la commande avec l'id: {}", id);
        OrderResponseDTO order = orderRepository.findById(id).map(orderMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        log.info("Commande recuperée: {}", order);
        return order;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        if (userNotFound(orderRequestDTO.getUserId())) throw new ResourceNotFoundException("User", "id", orderRequestDTO.getUserId());
        log.debug("Création de la commande pour le user: {}", orderRequestDTO.getUserId());
        Order order = orderMapper.toEntity(orderRequestDTO);
        Order savedOrder = orderRepository.save(order);
        orderMetrics.incrementCreatedOrders(savedOrder.getStatus());
        log.info("Commande crée: {}", savedOrder.getId());
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        if (userNotFound(id)) throw new ResourceNotFoundException("User", "id", id);
        log.debug("Mise à jour du statut de la commande avec l'id: {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        if (order.getStatus().equals(OrderStatus.CANCELLED) || order.getStatus().equals(OrderStatus.DELIVERED)) {
            log.error("La commande est annulée ou livrée, impossible de mettre à jour le statut.");
            throw new CommandCancelleOrDeliveredException("La commande est annulée ou livrée, impossible de mettre à jour le statut.", order.getStatus());
        }
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order savedOrder = orderRepository.save(order);
        log.info("Statut de la commande mis à jour: {}", savedOrder.getStatus());
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (userNotFound(id)) throw new ResourceNotFoundException("User", "id", id);
        log.debug("Suppression de la commande avec l'id: {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        orderRepository.delete(order);
        log.info("Commande supprimée: {}", id);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        if (userNotFound(userId)) throw new ResourceNotFoundException("User", "id", userId);
        log.debug("Récupération de toutes les commandes pour l'utilisateur avec l'id: {}", userId);
        List<OrderResponseDTO> orders = orderRepository.findByUserId(userId).stream().map(orderMapper::toDTO).toList();
        log.info("Nombre de commandes recuperées: {}", orders.size());
        return orders;
    }

    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        log.debug("Récupération de toutes les commandes avec le statut: {}", status);
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        List<Order> orders = orderRepository.findByStatus(orderStatus);
        log.info("Nombre de commandes recuperées: {}", orders.size());
        return orders.stream().map(orderMapper::toDTO).toList();
    }
}
