package com.bootcamp.stockservice.dto;

import com.bootcamp.stockservice.entity.ReservationStatus;
import com.bootcamp.stockservice.entity.StockReservation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Single reservation record")
public class ReservationResponse {

    @Schema(description = "Reservation ID", example = "501")
    private Long id;

    @Schema(description = "Order ID this reservation belongs to", example = "1001")
    private Long orderId;

    @Schema(description = "Product ID", example = "42")
    private Long productId;

    @Schema(description = "Reserved quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Reservation lifecycle status",
            example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED"})
    private ReservationStatus status;

    @Schema(description = "Creation timestamp", example = "2026-05-03T10:00:00")
    private LocalDateTime createdAt;

    public ReservationResponse() {
    }

    public ReservationResponse(Long id, Long orderId, Long productId, Integer quantity,
                               ReservationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static ReservationResponse from(StockReservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getOrderId(),
                reservation.getProductId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt()
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
