package com.bootcamp.productservice.service;

import com.bootcamp.productservice.dto.MessageResponse;
import com.bootcamp.productservice.dto.PageResponse;
import com.bootcamp.productservice.dto.ProductRequest;
import com.bootcamp.productservice.dto.ProductResponse;
import com.bootcamp.productservice.entity.Product;
import com.bootcamp.productservice.exception.DuplicateSkuException;
import com.bootcamp.productservice.exception.InsufficientStockException;
import com.bootcamp.productservice.exception.ResourceNotFoundException;
import com.bootcamp.productservice.repository.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAll(Pageable pageable) {
        return PageResponse.from(productRepository.findByActiveTrue(pageable), ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getByCategory(String category, Pageable pageable) {
        return PageResponse.from(
                productRepository.findByCategoryAndActiveTrue(category, pageable),
                ProductResponse::from
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String name, Pageable pageable) {
        return PageResponse.from(
                productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable),
                ProductResponse::from
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (Boolean.FALSE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        if (Boolean.FALSE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not found with SKU: " + sku);
        }
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("SKU already exists: " + request.getSku());
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.getActive() == null ? Boolean.TRUE : request.getActive());

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("SKU already exists: " + request.getSku());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public MessageResponse delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setActive(false);
        productRepository.save(product);
        return new MessageResponse("Product deactivated: " + id);
    }

    @Transactional
    public MessageResponse hardDelete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
        return new MessageResponse("Product permanently deleted: " + id);
    }

    @Transactional
    public ProductResponse decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (Boolean.FALSE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        product.setStock(product.getStock() + quantity);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse checkStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return ProductResponse.from(product);
    }
}
