package com.orders.infrastructure.exception;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String value) {
        super(String.format("Le statut de la commande '%s' n'est pas valide", value));
    }
}
