package com.bootcamp.paymentservice.config;

import com.bootcamp.paymentservice.event.PaymentRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_REQUEST_QUEUE = "payment.request.queue";
    public static final String PAYMENT_RESPONSE_QUEUE = "payment.response.queue";
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "payment.request";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY = "payment.response";

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(PAYMENT_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(PAYMENT_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentExchange).with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentResponseBinding(Queue paymentResponseQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentResponseQueue).to(paymentExchange).with(PAYMENT_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(om);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.bootcamp.*", "java.util", "java.lang");
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(
                "com.bootcamp.orderservice.saga.event.PaymentRequestEvent",
                PaymentRequestEvent.class
        );
        typeMapper.setIdClassMapping(idClassMapping);
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.AUTO);
        factory.setConcurrentConsumers(1);
        return factory;
    }
}
