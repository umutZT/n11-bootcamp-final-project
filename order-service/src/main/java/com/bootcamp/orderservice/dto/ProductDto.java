package com.bootcamp.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;

    public ProductDto() {
    }

    public ProductDto(Long id, String name, BigDecimal price, Integer stock, Boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.active = active;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
