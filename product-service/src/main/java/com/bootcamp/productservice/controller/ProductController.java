package com.bootcamp.productservice.controller;

import com.bootcamp.productservice.dto.MessageResponse;
import com.bootcamp.productservice.dto.PageResponse;
import com.bootcamp.productservice.dto.ProductRequest;
import com.bootcamp.productservice.dto.ProductResponse;
import com.bootcamp.productservice.exception.UnauthorizedException;
import com.bootcamp.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Products", description = "Product catalog: browse (public) and manage (admin only)")
public class ProductController {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String ROLE_HEADER = "X-User-Role";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List all products (paged)",
            description = "Public endpoint. Returns active products with pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products")
    })
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Operation(summary = "Get product by SKU")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirements
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getBySku(sku));
    }

    @Operation(summary = "List products by category (paged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products in category")
    })
    @SecurityRequirements
    @GetMapping("/category/{category}")
    public ResponseEntity<PageResponse<ProductResponse>> getByCategory(@PathVariable String category,
                                                                       Pageable pageable) {
        return ResponseEntity.ok(productService.getByCategory(category, pageable));
    }

    @Operation(summary = "Search products by name (case-insensitive contains)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of matching products")
    })
    @SecurityRequirements
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> search(@RequestParam String name,
                                                                Pageable pageable) {
        return ResponseEntity.ok(productService.search(name, pageable));
    }

    @Operation(summary = "Create a new product",
            description = "Requires ADMIN role. Creates a product with unique SKU.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request,
                                                  HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(productService.create(request));
    }

    @Operation(summary = "Update an existing product",
            description = "Requires ADMIN role. Replaces all editable fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest request,
                                                  HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(summary = "Soft-delete a product",
            description = "Requires ADMIN role. Marks the product as inactive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deactivated"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(productService.delete(id));
    }

    @Operation(summary = "Hard-delete a product",
            description = "Requires ADMIN role. Permanently removes the product row.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product permanently deleted"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<MessageResponse> hardDelete(@PathVariable Long id,
                                                      HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(productService.hardDelete(id));
    }

    @Operation(summary = "Decrease stock (internal saga step)",
            description = "Called by stock-service during saga. Decrements available stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock decreased"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}/stock/decrease")
    public ResponseEntity<ProductResponse> decreaseStock(@PathVariable Long id,
                                                         @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.decreaseStock(id, quantity));
    }

    @Operation(summary = "Increase stock (compensation step)",
            description = "Called when a saga compensates after a failed payment, restoring reserved units.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock increased"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}/stock/increase")
    public ResponseEntity<ProductResponse> increaseStock(@PathVariable Long id,
                                                         @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.increaseStock(id, quantity));
    }

    private void requireAdmin(HttpServletRequest request) {
        String role = request.getHeader(ROLE_HEADER);
        if (role == null || !ADMIN_ROLE.equals(role)) {
            throw new UnauthorizedException("Admin role required");
        }
    }
}
