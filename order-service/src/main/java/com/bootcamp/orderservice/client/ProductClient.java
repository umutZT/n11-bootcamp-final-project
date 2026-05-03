package com.bootcamp.orderservice.client;

import com.bootcamp.orderservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE", path = "/api/product")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);
}
