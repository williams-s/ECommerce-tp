package com.products.infrastructure.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(String service) {
        super("JWT invalide ou absent et rejete par " + service);
    }
}
