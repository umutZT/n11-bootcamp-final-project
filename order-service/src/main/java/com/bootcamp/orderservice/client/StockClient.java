package com.bootcamp.orderservice.client;

import com.bootcamp.orderservice.client.dto.MessageResponse;
import com.bootcamp.orderservice.client.dto.ReserveStockRequest;
import com.bootcamp.orderservice.client.dto.ReserveStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "STOCK-SERVICE", path = "/api/stock")
public interface StockClient {

    @PostMapping("/reserve")
    ReserveStockResponse reserveStock(@RequestBody ReserveStockRequest request);

    @PostMapping("/confirm/{orderId}")
    MessageResponse confirmReservations(@PathVariable("orderId") Long orderId);

    @PostMapping("/cancel/{orderId}")
    MessageResponse cancelReservations(@PathVariable("orderId") Long orderId);
}
