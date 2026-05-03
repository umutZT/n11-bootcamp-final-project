package com.bootcamp.orderservice.client.dto;

import java.util.List;

public class ReserveStockRequest {

    private Long orderId;
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
