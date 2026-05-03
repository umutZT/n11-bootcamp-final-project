package com.bootcamp.paymentservice.listener;

import com.bootcamp.paymentservice.config.RabbitMQConfig;
import com.bootcamp.paymentservice.event.PaymentRequestEvent;
import com.bootcamp.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestListener.class);

    private final PaymentService paymentService;

    public PaymentRequestListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_REQUEST_QUEUE)
    public void onPaymentRequest(PaymentRequestEvent request) {
        log.info("Received payment request for order {}", request.getOrderId());
        paymentService.processPaymentRequest(request);
    }
}
