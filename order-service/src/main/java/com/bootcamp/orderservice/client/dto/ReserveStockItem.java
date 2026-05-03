package com.bootcamp.orderservice.client.dto;

public class ReserveStockItem {

    private Long productId;
    private Integer quantity;

    public ReserveStockItem() {
    }

    public ReserveStockItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
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
}
