package com.products.infrastructure.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String value) {
        super(String.format("Le statut de la commande '%s' n'est pas valide", value));
    }
}
