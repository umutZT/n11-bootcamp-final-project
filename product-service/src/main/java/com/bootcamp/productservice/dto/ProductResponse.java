package com.bootcamp.productservice.dto;

import com.bootcamp.productservice.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Product representation")
public class ProductResponse {

    @Schema(description = "Database ID", example = "42")
    private Long id;

    @Schema(description = "Product display name", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "Long-form description", example = "Apple flagship with A17 Pro chip and 256GB storage")
    private String description;

    @Schema(description = "Unit price", example = "45000.00")
    private BigDecimal price;

    @Schema(description = "Available stock count", example = "50")
    private Integer stock;

    @Schema(description = "Product category", example = "Electronics")
    private String category;

    @Schema(description = "Brand name", example = "Apple")
    private String brand;

    @Schema(description = "Unique SKU code", example = "IPH15-PRO-256")
    private String sku;

    @Schema(description = "Product image URL", example = "https://cdn.example.com/iphone15pro.jpg")
    private String imageUrl;

    @Schema(description = "Whether the product is listed/active", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp", example = "2026-05-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-05-02T14:30:00")
    private LocalDateTime updatedAt;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock,
                           String category, String brand, String sku, String imageUrl, Boolean active,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.sku = sku;
        this.imageUrl = imageUrl;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getBrand(),
                product.getSku(),
                product.getImageUrl(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
