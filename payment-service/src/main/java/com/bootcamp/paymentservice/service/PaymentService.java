package com.bootcamp.paymentservice.service;

import com.bootcamp.paymentservice.config.RabbitMQConfig;
import com.bootcamp.paymentservice.entity.Payment;
import com.bootcamp.paymentservice.entity.PaymentStatus;
import com.bootcamp.paymentservice.event.PaymentRequestEvent;
import com.bootcamp.paymentservice.event.PaymentResponseEvent;
import com.bootcamp.paymentservice.iyzico.IyzicoPaymentClient;
import com.bootcamp.paymentservice.iyzico.IyzicoResult;
import com.bootcamp.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final IyzicoPaymentClient iyzicoClient;
    private final RabbitTemplate rabbitTemplate;

    public PaymentService(PaymentRepository paymentRepository,
                          IyzicoPaymentClient iyzicoClient,
                          RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.iyzicoClient = iyzicoClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void processPaymentRequest(PaymentRequestEvent request) {
        log.info("Processing payment for order {} (card ****{})",
                request.getOrderId(), maskLast4(request.getCardNumber()));

        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            log.warn("Payment already exists for order {}, skipping", request.getOrderId());
            return;
        }

        String conversationId = "ORD-" + request.getOrderId() + "-" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUsername(request.getUsername());
        payment.setAmount(request.getAmount());
        payment.setCurrency("TRY");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIyzicoConversationId(conversationId);
        payment.setMaskedCardNumber("****" + maskLast4(request.getCardNumber()));
        payment = paymentRepository.save(payment);

        PaymentResponseEvent response;
        try {
            IyzicoResult result = iyzicoClient.charge(request, conversationId);
            if (result.isSuccess()) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setIyzicoPaymentId(result.getPaymentId());
                paymentRepository.save(payment);
                response = new PaymentResponseEvent(
                        request.getOrderId(), true, result.getPaymentId(),
                        null, LocalDateTime.now());
                log.info("Payment SUCCEEDED for order {} (txn {})",
                        request.getOrderId(), result.getPaymentId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(result.getErrorMessage());
                paymentRepository.save(payment);
                response = new PaymentResponseEvent(
                        request.getOrderId(), false, null,
                        result.getErrorMessage(), LocalDateTime.now());
                log.info("Payment FAILED for order {}: {}",
                        request.getOrderId(), result.getErrorMessage());
            }
        } catch (Exception ex) {
            log.error("Iyzico call failed for order {}", request.getOrderId(), ex);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment gateway error: " + ex.getMessage());
            paymentRepository.save(payment);
            response = new PaymentResponseEvent(
                    request.getOrderId(), false, null,
                    "Payment gateway error", LocalDateTime.now());
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_RESPONSE_ROUTING_KEY,
                response
        );
    }

    private static String maskLast4(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
