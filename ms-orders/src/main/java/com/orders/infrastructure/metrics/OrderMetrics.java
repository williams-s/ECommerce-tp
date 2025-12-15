package com.orders.infrastructure.metrics;

import com.orders.domain.entity.Order;
import com.orders.domain.enums.OrderStatus;
import com.orders.domain.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class OrderMetrics {

    private final OrderRepository orderRepository;
    private final MeterRegistry meterRegistry;
    public OrderMetrics(OrderRepository orderRepository, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        Gauge.builder("orders.total.amount.today", this,
                        OrderMetrics::getTotalAmountToday)
                .description("Montant total des commandes du jour")
                .register(meterRegistry);
    }

    public void incrementCreatedOrders(OrderStatus status) {
        meterRegistry.counter("orders.created.total", "status", status.name()).increment();
    }

    public double getTotalAmountToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        BigDecimal totalAmount =
                orderRepository
                .findByOrderDateBetween(startOfDay, endOfDay)
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.doubleValue();
    }
}
