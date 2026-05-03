package com.bootcamp.stockservice.service;

import com.bootcamp.stockservice.client.ProductClient;
import com.bootcamp.stockservice.dto.ProductDto;
import com.bootcamp.stockservice.dto.ReserveStockItem;
import com.bootcamp.stockservice.dto.ReserveStockRequest;
import com.bootcamp.stockservice.dto.ReserveStockResponse;
import com.bootcamp.stockservice.entity.StockReservation;
import com.bootcamp.stockservice.repository.StockReservationRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockReservationService — reserve and partial rollback")
class StockReservationServiceTest {

    @Mock private StockReservationRepository reservationRepository;
    @Mock private ProductClient productClient;
    @InjectMocks private StockReservationService stockService;

    @Test
    @DisplayName("reserveStock happy path: all items reserved, status PENDING")
    void reserve_happyPath_allPending() {
        when(reservationRepository.save(any(StockReservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(productClient.decreaseStock(eq(1L), eq(2))).thenReturn(new ProductDto());

        ReserveStockRequest req = new ReserveStockRequest();
        req.setOrderId(500L);
        req.setItems(List.of(new ReserveStockItem(1L, 2)));

        ReserveStockResponse resp = stockService.reserveStock(req);

        assertThat(resp.isSuccess()).isTrue();
        verify(productClient).decreaseStock(1L, 2);
        verify(productClient, never()).increaseStock(any(), any());
    }

    @Test
    @DisplayName("Partial rollback: 2nd item fails → 1st item's stock restored")
    void reserve_secondFails_rollsBackFirst() {
        when(reservationRepository.save(any(StockReservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(productClient.decreaseStock(eq(1L), eq(1))).thenReturn(new ProductDto());

        Map<String, Collection<String>> headers = Map.of();
        Request request = Request.create(Request.HttpMethod.PUT, "/",
                headers, null, new RequestTemplate());
        Response response = Response.builder()
                .status(409)
                .reason("Insufficient")
                .request(request)
                .build();
        when(productClient.decreaseStock(eq(2L), eq(5)))
                .thenThrow(FeignException.errorStatus("decreaseStock", response));

        ReserveStockRequest req = new ReserveStockRequest();
        req.setOrderId(501L);
        req.setItems(List.of(
                new ReserveStockItem(1L, 1),
                new ReserveStockItem(2L, 5)
        ));

        ReserveStockResponse resp = stockService.reserveStock(req);

        assertThat(resp.isSuccess()).isFalse();
        verify(productClient).increaseStock(1L, 1);
    }
}
