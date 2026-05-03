package com.bootcamp.orderservice.saga;

import com.bootcamp.orderservice.config.RabbitMQConfig;
import com.bootcamp.orderservice.saga.event.PaymentResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponseListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResponseListener.class);

    private final OrderSagaOrchestrator orchestrator;

    public PaymentResponseListener(OrderSagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESPONSE_QUEUE)
    public void onPaymentResponse(PaymentResponseEvent event) {
        log.info("Received payment response for order {}: success={}",
                event.getOrderId(), event.isSuccess());
        orchestrator.handlePaymentResponse(event);
    }
}
