package com.orders.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orders.infrastructure.exception.InvalidOrderStatusException;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    DELIVERED,
    SHIPPED;

    @JsonCreator
    public static OrderStatus fromString(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException(value);
        }
    }

}
