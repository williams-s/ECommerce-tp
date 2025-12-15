package com.orders.infrastructure.exception;

import com.orders.domain.enums.OrderStatus;

public class CommandCancelleOrDeliveredException extends RuntimeException {
    public CommandCancelleOrDeliveredException(String message) {
        super(message);
    }

    public CommandCancelleOrDeliveredException(String message, OrderStatus status) {
        super(String.format("%s. Status: %s", message, status.toString().toLowerCase()));
    }
}
