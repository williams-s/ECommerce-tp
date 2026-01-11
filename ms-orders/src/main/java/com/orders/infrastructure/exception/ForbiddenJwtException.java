package com.orders.infrastructure.exception;

public class ForbiddenJwtException extends RuntimeException {
    public ForbiddenJwtException(String service) {
        super("JWT expired for " + service);
    }
}
