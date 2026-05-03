package com.bootcamp.orderservice.entity;

public enum SagaStatus {
    STARTED,
    STOCK_RESERVING,
    STOCK_RESERVED,
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    STOCK_CONFIRMED,
    COMPENSATING_STOCK,
    COMPENSATED,
    FAILED_AT_STOCK
}
