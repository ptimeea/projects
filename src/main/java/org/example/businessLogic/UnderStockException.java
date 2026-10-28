package org.example.businessLogic;
/**
 * Exception for when the warehouse does not have enough products.
 */

public class UnderStockException extends RuntimeException {
    public UnderStockException(String message) {
        super(message);
    }
}
