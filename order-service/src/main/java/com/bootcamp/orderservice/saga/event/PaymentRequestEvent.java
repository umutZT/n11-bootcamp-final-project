package com.bootcamp.orderservice.saga.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment request event sent to payment-service via RabbitMQ.
 * SECURITY NOTE: Contains card data — never log full card numbers,
 * never persist this DTO to any database. Card data is forwarded to
 * Iyzico and discarded.
 */
public class PaymentRequestEvent {

    private Long orderId;
    private String username;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String cardHolderName;
    private String cardNumber;
    private String expireMonth;
    private String expireYear;
    private String cvc;

    public PaymentRequestEvent() {
    }

    public PaymentRequestEvent(Long orderId, String username, BigDecimal amount,
                               LocalDateTime timestamp, String cardHolderName,
                               String cardNumber, String expireMonth, String expireYear,
                               String cvc) {
        this.orderId = orderId;
        this.username = username;
        this.amount = amount;
        this.timestamp = timestamp;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
        this.cvc = cvc;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
