package com.bootcamp.orderservice.service;

import com.bootcamp.orderservice.client.ProductClient;
import com.bootcamp.orderservice.dto.CreateOrderItemRequest;
import com.bootcamp.orderservice.dto.CreateOrderRequest;
import com.bootcamp.orderservice.dto.OrderResponse;
import com.bootcamp.orderservice.dto.ProductDto;
import com.bootcamp.orderservice.entity.Order;
import com.bootcamp.orderservice.entity.OrderItem;
import com.bootcamp.orderservice.exception.ResourceNotFoundException;
import com.bootcamp.orderservice.repository.OrderRepository;
import com.bootcamp.orderservice.saga.OrderSagaOrchestrator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderSagaOrchestrator orchestrator;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        OrderSagaOrchestrator orchestrator) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orchestrator = orchestrator;
    }

    @Transactional
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        Order order = new Order();
        order.setUsername(username);

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderItemRequest item : request.getItems()) {
            ProductDto product;
            try {
                product = productClient.getProduct(item.getProductId());
            } catch (Exception ex) {
                throw new ResourceNotFoundException("Product not found: " + item.getProductId());
            }
            if (product == null || !Boolean.TRUE.equals(product.getActive())) {
                throw new ResourceNotFoundException("Product not available: " + item.getProductId());
            }

            OrderItem oi = new OrderItem();
            oi.setProductId(product.getId());
            oi.setProductName(product.getName());
            oi.setQuantity(item.getQuantity());
            oi.setUnitPrice(product.getPrice());
            oi.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            order.addItem(oi);
            total = total.add(oi.getSubtotal());
        }
        order.setTotalAmount(total);

        OrderSagaOrchestrator.CardData cardData = new OrderSagaOrchestrator.CardData(
                request.getCardHolderName(),
                request.getCardNumber(),
                request.getExpireMonth(),
                request.getExpireYear(),
                request.getCvc()
        );
        Order saved = orchestrator.startSaga(order, cardData);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orchestrator.continueAfterCreate(saved.getId());
            }
        });

        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id, String username) {
        Order order = orderRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String username) {
        return orderRepository.findByUsernameOrderByCreatedAtDesc(username).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
