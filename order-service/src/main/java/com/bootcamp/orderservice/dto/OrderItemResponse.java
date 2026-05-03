package com.bootcamp.orderservice.dto;

import com.bootcamp.orderservice.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Order line item snapshot")
public class OrderItemResponse {

    @Schema(description = "Item ID", example = "5001")
    private Long id;

    @Schema(description = "Product ID", example = "42")
    private Long productId;

    @Schema(description = "Snapshot of product name at order time", example = "iPhone 15 Pro")
    private String productName;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price at order time", example = "45000.00")
    private BigDecimal unitPrice;

    @Schema(description = "Line subtotal (unitPrice * quantity)", example = "90000.00")
    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, Long productId, String productName, Integer quantity,
                             BigDecimal unitPrice, BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
