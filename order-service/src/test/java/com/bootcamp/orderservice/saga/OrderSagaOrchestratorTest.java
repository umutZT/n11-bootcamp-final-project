package com.bootcamp.orderservice.saga;

import com.bootcamp.orderservice.client.StockClient;
import com.bootcamp.orderservice.client.dto.MessageResponse;
import com.bootcamp.orderservice.client.dto.ReserveStockResponse;
import com.bootcamp.orderservice.config.RabbitMQConfig;
import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.OrderItem;
import com.bootcamp.orderservice.entity.OrderStatus;
import com.bootcamp.orderservice.entity.SagaStatus;
import com.bootcamp.orderservice.repository.OrderRepository;
import com.bootcamp.orderservice.saga.event.PaymentResponseEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderSagaOrchestrator — saga state transitions and compensation")
class OrderSagaOrchestratorTest {

    @Mock private OrderRepository orderRepository;
    @Mock private StockClient stockClient;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private OrderSagaOrchestrator orchestrator;

    private Order makeOrder(Long id) {
        Order o = new Order();
        o.setId(id);
        o.setUsername("ahmet");
        o.setTotalAmount(new BigDecimal("100.00"));
        o.setStatus(OrderStatus.PENDING);
        o.setSagaStatus(SagaStatus.STARTED);
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setSubtotal(new BigDecimal("100.00"));
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        o.setItems(items);
        return o;
    }

    @Test
    @DisplayName("reserveStockStep: stock OK → publishes payment request")
    void reserveStockStep_success_publishesPaymentRequest() {
        Order order = makeOrder(10L);
        OrderSagaOrchestrator.CardData card = new OrderSagaOrchestrator.CardData(
                "Test User", "5528790000000008", "12", "2030", "123");
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        orchestrator.startSaga(order, card);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        ReserveStockResponse resp = new ReserveStockResponse();
        resp.setSuccess(true);
        when(stockClient.reserveStock(any())).thenReturn(resp);

        orchestrator.reserveStockStep(10L);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.PAYMENT_EXCHANGE),
                eq(RabbitMQConfig.PAYMENT_REQUEST_ROUTING_KEY),
                any(Object.class));
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.PAYMENT_PROCESSING);
    }

    @Test
    @DisplayName("reserveStockStep: stock FAIL → no payment, order CANCELLED")
    void reserveStockStep_stockFailure_marksFailedAtStock() {
        Order order = makeOrder(11L);
        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        ReserveStockResponse resp = new ReserveStockResponse();
        resp.setSuccess(false);
        resp.setFailureReason("Insufficient stock for product 1");
        when(stockClient.reserveStock(any())).thenReturn(resp);

        orchestrator.reserveStockStep(11L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.FAILED_AT_STOCK);
        assertThat(order.getFailureReason()).contains("Insufficient stock");
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
        verify(stockClient, never()).cancelReservations(anyLong());
    }

    @Test
    @DisplayName("reserveStockStep: Feign exception → marks failed at stock")
    void reserveStockStep_feignException_marksFailed() {
        Order order = makeOrder(12L);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockClient.reserveStock(any()))
                .thenThrow(new RuntimeException("Connection timeout"));

        orchestrator.reserveStockStep(12L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.FAILED_AT_STOCK);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    @DisplayName("handlePaymentResponse: SUCCESS → confirms stock, order CONFIRMED")
    void handlePaymentResponse_success_confirmsStock() {
        Order order = makeOrder(13L);
        order.setSagaStatus(SagaStatus.PAYMENT_PROCESSING);
        when(orderRepository.findById(13L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockClient.confirmReservations(13L)).thenReturn(new MessageResponse());

        PaymentResponseEvent event = new PaymentResponseEvent(
                13L, true, "TXN-12345", null, LocalDateTime.now());

        orchestrator.handlePaymentResponse(event);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.STOCK_CONFIRMED);
        verify(stockClient).confirmReservations(13L);
        verify(stockClient, never()).cancelReservations(anyLong());
    }

    @Test
    @DisplayName("COMPENSATION: payment FAIL → cancels stock, order CANCELLED")
    void handlePaymentResponse_failure_compensatesStock() {
        Order order = makeOrder(14L);
        order.setSagaStatus(SagaStatus.PAYMENT_PROCESSING);
        when(orderRepository.findById(14L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockClient.cancelReservations(14L)).thenReturn(new MessageResponse());

        PaymentResponseEvent event = new PaymentResponseEvent(
                14L, false, null, "Card declined", LocalDateTime.now());

        orchestrator.handlePaymentResponse(event);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.COMPENSATED);
        assertThat(order.getFailureReason()).contains("Card declined");

        verify(stockClient).cancelReservations(14L);
        verify(stockClient, never()).confirmReservations(anyLong());
    }
}
