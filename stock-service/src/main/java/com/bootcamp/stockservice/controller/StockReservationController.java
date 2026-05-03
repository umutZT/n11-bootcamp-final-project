package com.bootcamp.stockservice.controller;

import com.bootcamp.stockservice.dto.MessageResponse;
import com.bootcamp.stockservice.dto.ReservationResponse;
import com.bootcamp.stockservice.dto.ReserveStockRequest;
import com.bootcamp.stockservice.dto.ReserveStockResponse;
import com.bootcamp.stockservice.service.StockReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock Reservations",
        description = "Reservation ledger for saga orchestration. Internal endpoints called by order-service.")
public class StockReservationController {

    private final StockReservationService reservationService;

    public StockReservationController(StockReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Reserve stock for an order (saga step)",
            description = "Decrements product-service stock and creates PENDING reservations. Returns success=false with failureReason on insufficient stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation outcome (success or failure)",
                    content = @Content(schema = @Schema(implementation = ReserveStockResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/reserve")
    public ResponseEntity<ReserveStockResponse> reserve(@Valid @RequestBody ReserveStockRequest request) {
        return ResponseEntity.ok(reservationService.reserveStock(request));
    }

    @Operation(summary = "Confirm reservations after successful payment",
            description = "Marks the order's PENDING reservations as CONFIRMED. Stock is permanently deducted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations confirmed"),
            @ApiResponse(responseCode = "404", description = "No reservations for this order")
    })
    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<MessageResponse> confirm(@PathVariable Long orderId) {
        return ResponseEntity.ok(reservationService.confirmReservations(orderId));
    }

    @Operation(summary = "Cancel reservations and restore stock (compensation)",
            description = "Saga compensation step: marks reservations CANCELLED and increments product-service stock back.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations cancelled and stock restored"),
            @ApiResponse(responseCode = "404", description = "No reservations for this order")
    })
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<MessageResponse> cancel(@PathVariable Long orderId) {
        return ResponseEntity.ok(reservationService.cancelReservations(orderId));
    }

    @Operation(summary = "Get all reservations for an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of reservations (may be empty)")
    })
    @GetMapping("/reservations/{orderId}")
    public ResponseEntity<List<ReservationResponse>> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(reservationService.getByOrderId(orderId));
    }
}
