package com.orders.infrastructure.exception;

public class ProductOutOfStockException extends RuntimeException {

    public ProductOutOfStockException(String message) {
        super(message);
    }

    public ProductOutOfStockException(int stock, int quantity) {
        super(String.format("Le produit est en rupture de stock. Stock: %d, Quantity: %d", stock, quantity));
    }

}
