package com.bootcamp.orderservice.dto;

import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.OrderStatus;
import com.bootcamp.orderservice.entity.SagaStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Order representation including saga state")
public class OrderResponse {

    @Schema(description = "Order ID", example = "1001")
    private Long id;

    @Schema(description = "Owner username", example = "ahmet")
    private String username;

    @Schema(description = "Sum of item subtotals", example = "90000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Coarse-grained order status",
            example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED"})
    private OrderStatus status;

    @Schema(description = "Fine-grained saga step",
            example = "STARTED",
            allowableValues = {"STARTED", "STOCK_RESERVING", "STOCK_RESERVED", "PAYMENT_PROCESSING",
                    "PAYMENT_COMPLETED", "STOCK_CONFIRMED", "COMPENSATING_STOCK", "COMPENSATED",
                    "FAILED_AT_STOCK"})
    private SagaStatus sagaStatus;

    @Schema(description = "Reason if the order was cancelled or compensated",
            example = "Insufficient stock for product 42")
    private String failureReason;

    @Schema(description = "Order line items")
    private List<OrderItemResponse> items;

    @Schema(description = "Creation timestamp", example = "2026-05-03T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-05-03T10:00:05")
    private LocalDateTime updatedAt;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String username, BigDecimal totalAmount, OrderStatus status,
                         SagaStatus sagaStatus, String failureReason, List<OrderItemResponse> items,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.totalAmount = totalAmount;
        this.status = status;
        this.sagaStatus = sagaStatus;
        this.failureReason = failureReason;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemDtos = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getUsername(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getSagaStatus(),
                order.getFailureReason(),
                itemDtos,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public SagaStatus getSagaStatus() {
        return sagaStatus;
    }

    public void setSagaStatus(SagaStatus sagaStatus) {
        this.sagaStatus = sagaStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
