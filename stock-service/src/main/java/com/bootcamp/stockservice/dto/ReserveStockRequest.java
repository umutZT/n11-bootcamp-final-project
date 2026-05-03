package com.bootcamp.stockservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Saga step input: reserve stock for the items of an order")
public class ReserveStockRequest {

    @Schema(description = "Order ID this reservation belongs to", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long orderId;

    @Schema(description = "Line items to reserve", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @Valid
    private List<ReserveStockItem> items;

    public ReserveStockRequest() {
    }

    public ReserveStockRequest(Long orderId, List<ReserveStockItem> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<ReserveStockItem> getItems() {
        return items;
    }

    public void setItems(List<ReserveStockItem> items) {
        this.items = items;
    }
}
