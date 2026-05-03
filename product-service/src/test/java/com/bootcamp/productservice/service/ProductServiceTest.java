package com.bootcamp.productservice.service;

import com.bootcamp.productservice.dto.ProductRequest;
import com.bootcamp.productservice.entity.Product;
import com.bootcamp.productservice.exception.DuplicateSkuException;
import com.bootcamp.productservice.exception.InsufficientStockException;
import com.bootcamp.productservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — SKU uniqueness and stock guards")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    @Test
    @DisplayName("create: rejects duplicate SKU with 409")
    void create_duplicateSku_throws() {
        when(productRepository.existsBySku("DUPE-001")).thenReturn(true);

        ProductRequest req = new ProductRequest();
        req.setName("Test");
        req.setPrice(new BigDecimal("10"));
        req.setStock(5);
        req.setCategory("Test");
        req.setSku("DUPE-001");

        assertThatThrownBy(() -> productService.create(req))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    @DisplayName("decreaseStock: throws InsufficientStockException when not enough")
    void decreaseStock_insufficient_throws() {
        Product p = new Product();
        p.setId(1L);
        p.setStock(2);
        p.setActive(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> productService.decreaseStock(1L, 5))
                .isInstanceOf(InsufficientStockException.class);
    }
}
