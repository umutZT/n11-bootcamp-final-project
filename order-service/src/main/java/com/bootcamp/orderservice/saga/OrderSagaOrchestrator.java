package com.bootcamp.orderservice.saga;

import com.bootcamp.orderservice.client.StockClient;
import com.bootcamp.orderservice.client.dto.ReserveStockItem;
import com.bootcamp.orderservice.client.dto.ReserveStockRequest;
import com.bootcamp.orderservice.client.dto.ReserveStockResponse;
import com.bootcamp.orderservice.config.RabbitMQConfig;
import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.OrderStatus;
import com.bootcamp.orderservice.entity.SagaStatus;
import com.bootcamp.orderservice.repository.OrderRepository;
import com.bootcamp.orderservice.saga.event.PaymentRequestEvent;
import com.bootcamp.orderservice.saga.event.PaymentResponseEvent;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);

    private final OrderRepository orderRepository;
    private final StockClient stockClient;
    private final RabbitTemplate rabbitTemplate;

    private final Map<Long, CardData> pendingCardData = new ConcurrentHashMap<>();

    public OrderSagaOrchestrator(OrderRepository orderRepository,
                                 StockClient stockClient,
                                 RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.stockClient = stockClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public record CardData(String cardHolderName, String cardNumber,
                           String expireMonth, String expireYear, String cvc) {
    }

    @Transactional
    public Order startSaga(Order order, CardData cardData) {
        order.setStatus(OrderStatus.PENDING);
        order.setSagaStatus(SagaStatus.STARTED);
        Order saved = orderRepository.save(order);
        pendingCardData.put(saved.getId(), cardData);
        return saved;
    }

    public void continueAfterCreate(Long orderId) {
        reserveStockStep(orderId);
    }

    @Transactional
    public void reserveStockStep(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        order.setSagaStatus(SagaStatus.STOCK_RESERVING);
        orderRepository.save(order);

        List<ReserveStockItem> items = order.getItems().stream()
                .map(oi -> new ReserveStockItem(oi.getProductId(), oi.getQuantity()))
                .toList();
        ReserveStockRequest request = new ReserveStockRequest(orderId, items);

        try {
            ReserveStockResponse response = stockClient.reserveStock(request);
            if (response != null && response.isSuccess()) {
                order.setSagaStatus(SagaStatus.STOCK_RESERVED);
                orderRepository.save(order);
                publishPaymentRequest(order);
            } else {
                String reason = response == null ? "null response" : response.getFailureReason();
                order.setSagaStatus(SagaStatus.FAILED_AT_STOCK);
                order.setStatus(OrderStatus.CANCELLED);
                order.setFailureReason("Stock reservation failed: " + reason);
                orderRepository.save(order);
                pendingCardData.remove(orderId);
                log.warn("Saga FAILED_AT_STOCK for order {}: {}", orderId, reason);
            }
        } catch (FeignException ex) {
            order.setSagaStatus(SagaStatus.FAILED_AT_STOCK);
            order.setStatus(OrderStatus.CANCELLED);
            order.setFailureReason("Stock service error: " + ex.getMessage());
            orderRepository.save(order);
            pendingCardData.remove(orderId);
            log.error("Stock service Feign error for order {}: status={}", orderId, ex.status());
        } catch (RuntimeException ex) {
            order.setSagaStatus(SagaStatus.FAILED_AT_STOCK);
            order.setStatus(OrderStatus.CANCELLED);
            order.setFailureReason("Stock service error: " + ex.getMessage());
            orderRepository.save(order);
            pendingCardData.remove(orderId);
            log.error("Stock service error for order {}: {}", orderId, ex.getMessage());
        }
    }

    @Transactional
    public void publishPaymentRequest(Order order) {
        order.setSagaStatus(SagaStatus.PAYMENT_PROCESSING);
        orderRepository.save(order);

        CardData card = pendingCardData.remove(order.getId());
        if (card == null) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setFailureReason("Card data missing for payment");
            order.setSagaStatus(SagaStatus.COMPENSATING_STOCK);
            orderRepository.save(order);
            log.error("Card data missing for order {} - compensating stock", order.getId());
            compensateStockStep(order);
            return;
        }

        PaymentRequestEvent event = new PaymentRequestEvent(
                order.getId(),
                order.getUsername(),
                order.getTotalAmount(),
                LocalDateTime.now(),
                card.cardHolderName(),
                card.cardNumber(),
                card.expireMonth(),
                card.expireYear(),
                card.cvc()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_REQUEST_ROUTING_KEY,
                event
        );
        log.info("Payment request published for order {} (card ending {})",
                order.getId(), maskCardNumber(card.cardNumber()));
    }

    @Transactional
    public void handlePaymentResponse(PaymentResponseEvent response) {
        Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new IllegalStateException(
                        "Order " + response.getOrderId() + " not found for payment response"));

        if (response.isSuccess()) {
            order.setSagaStatus(SagaStatus.PAYMENT_COMPLETED);
            orderRepository.save(order);
            confirmStockStep(order);
        } else {
            order.setSagaStatus(SagaStatus.COMPENSATING_STOCK);
            order.setFailureReason("Payment failed: " + response.getFailureReason());
            orderRepository.save(order);
            compensateStockStep(order);
        }
    }

    @Transactional
    public void confirmStockStep(Order order) {
        try {
            stockClient.confirmReservations(order.getId());
            order.setSagaStatus(SagaStatus.STOCK_CONFIRMED);
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info("Saga COMPLETED successfully for order {}", order.getId());
        } catch (Exception ex) {
            log.error("CRITICAL: Payment succeeded but stock confirm failed for order {}",
                    order.getId(), ex);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setFailureReason("Stock confirm failed (manual review needed): " + ex.getMessage());
            orderRepository.save(order);
        }
    }

    @Transactional
    public void compensateStockStep(Order order) {
        try {
            stockClient.cancelReservations(order.getId());
            order.setSagaStatus(SagaStatus.COMPENSATED);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Saga COMPENSATED for order {} - stock returned", order.getId());
        } catch (Exception ex) {
            log.error("Compensation failed for order {}", order.getId(), ex);
            order.setStatus(OrderStatus.CANCELLED);
            order.setFailureReason("Compensation failed: " + ex.getMessage());
            orderRepository.save(order);
        }
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
