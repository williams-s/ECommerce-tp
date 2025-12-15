package com.orders.infrastructure.exception;

public class ExternalServiceDownException extends RuntimeException {
    public ExternalServiceDownException(String message) {
        super(message);
    }

    public ExternalServiceDownException(String serviceName, String resourceName, String value) {
        super(String.format("Impossible de contacter le service %s, ressource %s : %s", serviceName, resourceName, value));
    }
}
