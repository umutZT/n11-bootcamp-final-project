package com.bootcamp.paymentservice.controller;

import com.bootcamp.paymentservice.dto.PaymentResponse;
import com.bootcamp.paymentservice.exception.ResourceNotFoundException;
import com.bootcamp.paymentservice.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payments",
        description = "Read-only payment records (writes happen via RabbitMQ from order-service)")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Operation(
            summary = "Get payment record by order ID",
            description = "Returns payment details including masked card and Iyzico transaction ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "No payment record for this order")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
    }
}
