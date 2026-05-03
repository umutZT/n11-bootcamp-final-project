package com.bootcamp.stockservice.client;

import com.bootcamp.stockservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE", path = "/api/product")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);

    @PutMapping("/{id}/stock/decrease")
    ProductDto decreaseStock(@PathVariable("id") Long id,
                             @RequestParam("quantity") Integer quantity);

    @PutMapping("/{id}/stock/increase")
    ProductDto increaseStock(@PathVariable("id") Long id,
                             @RequestParam("quantity") Integer quantity);
}
