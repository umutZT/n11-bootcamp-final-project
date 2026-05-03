package com.bootcamp.orderservice.service;

import com.bootcamp.orderservice.client.ProductClient;
import com.bootcamp.orderservice.dto.CreateOrderItemRequest;
import com.bootcamp.orderservice.dto.CreateOrderRequest;
import com.bootcamp.orderservice.dto.OrderResponse;
import com.bootcamp.orderservice.dto.ProductDto;
import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.OrderStatus;
import com.bootcamp.orderservice.entity.SagaStatus;
import com.bootcamp.orderservice.exception.ResourceNotFoundException;
import com.bootcamp.orderservice.repository.OrderRepository;
import com.bootcamp.orderservice.saga.OrderSagaOrchestrator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService — order creation and validation")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductClient productClient;
    @Mock private OrderSagaOrchestrator orchestrator;

    @InjectMocks private OrderService orderService;

    private ProductDto productAvailable;
    private ProductDto productInactive;

    @BeforeEach
    void setup() {
        productAvailable = new ProductDto();
        productAvailable.setId(1L);
        productAvailable.setName("Test Product");
        productAvailable.setPrice(new BigDecimal("100.00"));
        productAvailable.setStock(50);
        productAvailable.setActive(true);

        productInactive = new ProductDto();
        productInactive.setId(2L);
        productInactive.setName("Inactive Product");
        productInactive.setPrice(new BigDecimal("50.00"));
        productInactive.setStock(10);
        productInactive.setActive(false);

        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("createOrder: validates products, computes total, starts saga")
    void createOrder_happyPath_startsSaga() {
        when(productClient.getProduct(1L)).thenReturn(productAvailable);
        Order saved = new Order();
        saved.setId(99L);
        saved.setStatus(OrderStatus.PENDING);
        saved.setSagaStatus(SagaStatus.STARTED);
        when(orchestrator.startSaga(any(Order.class), any())).thenReturn(saved);

        CreateOrderRequest req = new CreateOrderRequest();
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        req.setItems(List.of(item));
        req.setCardHolderName("Test User");
        req.setCardNumber("5528790000000008");
        req.setExpireMonth("12");
        req.setExpireYear("2030");
        req.setCvc("123");

        OrderResponse result = orderService.createOrder("ahmet", req);

        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orchestrator).startSaga(any(Order.class), any());
    }

    @Test
    @DisplayName("createOrder: throws when product not found")
    void createOrder_productNotFound_throws() {
        when(productClient.getProduct(999L))
                .thenThrow(new RuntimeException("404 Not Found"));

        CreateOrderRequest req = new CreateOrderRequest();
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(999L);
        item.setQuantity(1);
        req.setItems(List.of(item));
        req.setCardHolderName("X Y");
        req.setCardNumber("5528790000000008");
        req.setExpireMonth("12");
        req.setExpireYear("2030");
        req.setCvc("123");

        assertThatThrownBy(() -> orderService.createOrder("ahmet", req))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(orchestrator);
    }

    @Test
    @DisplayName("createOrder: rejects inactive products")
    void createOrder_inactiveProduct_throws() {
        when(productClient.getProduct(2L)).thenReturn(productInactive);

        CreateOrderRequest req = new CreateOrderRequest();
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(2L);
        item.setQuantity(1);
        req.setItems(List.of(item));
        req.setCardHolderName("X Y");
        req.setCardNumber("5528790000000008");
        req.setExpireMonth("12");
        req.setExpireYear("2030");
        req.setCvc("123");

        assertThatThrownBy(() -> orderService.createOrder("ahmet", req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not available");

        verifyNoInteractions(orchestrator);
    }
}
