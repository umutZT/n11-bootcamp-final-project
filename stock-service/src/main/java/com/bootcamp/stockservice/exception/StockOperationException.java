package com.bootcamp.stockservice.exception;

public class StockOperationException extends RuntimeException {

    public StockOperationException(String message) {
        super(message);
    }

    public StockOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
