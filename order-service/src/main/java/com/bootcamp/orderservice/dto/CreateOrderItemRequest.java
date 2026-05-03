package com.bootcamp.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Order line item")
public class CreateOrderItemRequest {

    @Schema(description = "Product ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long productId;

    @Schema(description = "Quantity to order (>= 1)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    private Integer quantity;

    public CreateOrderItemRequest() {
    }

    public CreateOrderItemRequest(Long productId, Integer quantity) {
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
