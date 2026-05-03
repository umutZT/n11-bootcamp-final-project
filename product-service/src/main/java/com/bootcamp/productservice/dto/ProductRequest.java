package com.bootcamp.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Create or update product payload")
public class ProductRequest {

    @Schema(description = "Product display name", example = "iPhone 15 Pro", maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 200)
    private String name;

    @Schema(description = "Long-form description", example = "Apple flagship with A17 Pro chip and 256GB storage", maxLength = 2000)
    @Size(max = 2000)
    private String description;

    @Schema(description = "Unit price", example = "45000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @Schema(description = "Available stock count", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(0)
    private Integer stock;

    @Schema(description = "Product category", example = "Electronics", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 100)
    private String category;

    @Schema(description = "Brand name", example = "Apple", maxLength = 100)
    @Size(max = 100)
    private String brand;

    @Schema(description = "Unique SKU code", example = "IPH15-PRO-256", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 50)
    private String sku;

    @Schema(description = "Product image URL", example = "https://cdn.example.com/iphone15pro.jpg", maxLength = 500)
    @Size(max = 500)
    private String imageUrl;

    @Schema(description = "Whether the product is listed/active", example = "true")
    private Boolean active;

    public ProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
