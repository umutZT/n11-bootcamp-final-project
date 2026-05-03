package com.bootcamp.paymentservice.dto;

import com.bootcamp.paymentservice.entity.Payment;
import com.bootcamp.paymentservice.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Payment record")
public class PaymentResponse {

    @Schema(description = "Payment ID", example = "9001")
    private Long id;

    @Schema(description = "Order ID this payment belongs to", example = "1001")
    private Long orderId;

    @Schema(description = "Owner username", example = "ahmet")
    private String username;

    @Schema(description = "Charged amount", example = "90000.00")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "TRY")
    private String currency;

    @Schema(description = "Payment lifecycle status",
            example = "SUCCESS", allowableValues = {"PENDING", "SUCCESS", "FAILED"})
    private PaymentStatus status;

    @Schema(description = "Iyzico transaction ID (only when status=SUCCESS)", example = "31830264")
    private String iyzicoPaymentId;

    @Schema(description = "PCI-safe masked card number", example = "****0008")
    private String maskedCardNumber;

    @Schema(description = "Reason if payment failed", example = "Card declined by issuer")
    private String failureReason;

    @Schema(description = "Creation timestamp", example = "2026-05-03T10:00:02")
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(Long id, Long orderId, String username, BigDecimal amount, String currency,
                           PaymentStatus status, String iyzicoPaymentId, String maskedCardNumber,
                           String failureReason, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.username = username;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.iyzicoPaymentId = iyzicoPaymentId;
        this.maskedCardNumber = maskedCardNumber;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUsername(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getIyzicoPaymentId(),
                payment.getMaskedCardNumber(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getIyzicoPaymentId() {
        return iyzicoPaymentId;
    }

    public void setIyzicoPaymentId(String iyzicoPaymentId) {
        this.iyzicoPaymentId = iyzicoPaymentId;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
