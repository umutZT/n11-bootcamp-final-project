package com.bootcamp.paymentservice.service;

import com.bootcamp.paymentservice.entity.Payment;
import com.bootcamp.paymentservice.event.PaymentRequestEvent;
import com.bootcamp.paymentservice.event.PaymentResponseEvent;
import com.bootcamp.paymentservice.iyzico.IyzicoPaymentClient;
import com.bootcamp.paymentservice.iyzico.IyzicoResult;
import com.bootcamp.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService — Iyzico integration and idempotency")
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private IyzicoPaymentClient iyzicoClient;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private PaymentService paymentService;

    private PaymentRequestEvent makeRequest(Long orderId) {
        return new PaymentRequestEvent(
                orderId, "ahmet", new BigDecimal("100.00"), LocalDateTime.now(),
                "Test User", "5528790000000008", "12", "2030", "123");
    }

    @Test
    @DisplayName("Iyzico SUCCESS → persists SUCCESS, publishes success response")
    void processPayment_iyzicoSuccess_persistsAndPublishes() {
        when(paymentRepository.existsByOrderId(100L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        IyzicoResult success = new IyzicoResult();
        success.setSuccess(true);
        success.setPaymentId("31830264");
        when(iyzicoClient.charge(any(), anyString())).thenReturn(success);

        paymentService.processPaymentRequest(makeRequest(100L));

        ArgumentCaptor<PaymentResponseEvent> captor =
                ArgumentCaptor.forClass(PaymentResponseEvent.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());

        PaymentResponseEvent published = captor.getValue();
        assertThat(published.isSuccess()).isTrue();
        assertThat(published.getTransactionId()).isEqualTo("31830264");
        assertThat(published.getOrderId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Iyzico FAILURE → persists FAILED, publishes failure response")
    void processPayment_iyzicoFailure_persistsFailedAndPublishes() {
        when(paymentRepository.existsByOrderId(101L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        IyzicoResult fail = new IyzicoResult();
        fail.setSuccess(false);
        fail.setErrorMessage("Card declined");
        when(iyzicoClient.charge(any(), anyString())).thenReturn(fail);

        paymentService.processPaymentRequest(makeRequest(101L));

        ArgumentCaptor<PaymentResponseEvent> captor =
                ArgumentCaptor.forClass(PaymentResponseEvent.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());

        PaymentResponseEvent published = captor.getValue();
        assertThat(published.isSuccess()).isFalse();
        assertThat(published.getFailureReason()).contains("Card declined");
    }

    @Test
    @DisplayName("Iyzico exception → persists FAILED, publishes gateway error")
    void processPayment_iyzicoException_persistsFailedAndPublishes() {
        when(paymentRepository.existsByOrderId(102L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(iyzicoClient.charge(any(), anyString()))
                .thenThrow(new RuntimeException("Connection refused"));

        paymentService.processPaymentRequest(makeRequest(102L));

        ArgumentCaptor<PaymentResponseEvent> captor =
                ArgumentCaptor.forClass(PaymentResponseEvent.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), captor.capture());

        PaymentResponseEvent published = captor.getValue();
        assertThat(published.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("Idempotency: existing payment → skip, no Iyzico call, no publish")
    void processPayment_alreadyExists_skips() {
        when(paymentRepository.existsByOrderId(103L)).thenReturn(true);

        paymentService.processPaymentRequest(makeRequest(103L));

        verifyNoInteractions(iyzicoClient);
        verify(paymentRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}
