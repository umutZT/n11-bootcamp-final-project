package com.bootcamp.paymentservice.event;

import java.time.LocalDateTime;

public class PaymentResponseEvent {

    private Long orderId;
    private boolean success;
    private String transactionId;
    private String failureReason;
    private LocalDateTime timestamp;

    public PaymentResponseEvent() {
    }

    public PaymentResponseEvent(Long orderId, boolean success, String transactionId,
                                String failureReason, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.success = success;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
        this.timestamp = timestamp;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
