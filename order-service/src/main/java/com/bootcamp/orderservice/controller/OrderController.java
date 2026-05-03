package com.bootcamp.orderservice.controller;

import com.bootcamp.orderservice.dto.CreateOrderRequest;
import com.bootcamp.orderservice.dto.OrderResponse;
import com.bootcamp.orderservice.exception.UnauthorizedException;
import com.bootcamp.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Orders",
        description = "Order placement with saga orchestration. Async response (202): poll GET /{id} for final status.")
public class OrderController {

    private static final String USERNAME_HEADER = "X-User-Username";

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Create an order (starts saga)",
            description = """
                    Initiates the saga: creates order with status=PENDING, reserves stock,
                    publishes payment request to RabbitMQ. Returns immediately with PENDING
                    status; client should poll GET /{id} to observe final status (CONFIRMED
                    or CANCELLED).

                    Card data is forwarded to payment-service and Iyzico — never stored in DB.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Order accepted, saga started",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed (invalid card, missing fields)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request,
                                                HttpServletRequest httpRequest) {
        String username = requireUsername(httpRequest);
        OrderResponse response = orderService.createOrder(username, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "Get order by ID (own orders only)",
            description = "Returns the requester's order; will not expose other users' orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @ApiResponse(responseCode = "404", description = "Order not found for this user")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id,
                                                 HttpServletRequest httpRequest) {
        String username = requireUsername(httpRequest);
        return ResponseEntity.ok(orderService.getOrder(id, username));
    }

    @Operation(summary = "List the authenticated user's orders",
            description = "Returns all orders for the user, newest first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order list (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(HttpServletRequest httpRequest) {
        String username = requireUsername(httpRequest);
        return ResponseEntity.ok(orderService.getMyOrders(username));
    }

    private String requireUsername(HttpServletRequest request) {
        String username = request.getHeader(USERNAME_HEADER);
        if (username == null || username.isBlank()) {
            throw new UnauthorizedException("Missing user context");
        }
        return username;
    }
}
