package com.bootcamp.stockservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of a reserve-stock call")
public class ReserveStockResponse {

    @Schema(description = "Order ID", example = "1001")
    private Long orderId;

    @Schema(description = "Created reservations (when success=true)")
    private List<ReservationResponse> reservations;

    @Schema(description = "Whether the reservation succeeded", example = "true")
    private boolean success;

    @Schema(description = "Reason for failure (when success=false)", example = "Insufficient stock for product 42")
    private String failureReason;

    public ReserveStockResponse() {
    }

    public ReserveStockResponse(Long orderId, List<ReservationResponse> reservations,
                                boolean success, String failureReason) {
        this.orderId = orderId;
        this.reservations = reservations;
        this.success = success;
        this.failureReason = failureReason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<ReservationResponse> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
