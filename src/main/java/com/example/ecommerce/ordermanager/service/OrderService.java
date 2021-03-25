package com.example.ecommerce.ordermanager.service;

import com.example.ecommerce.ordermanager.entity.Order;
import com.example.ecommerce.ordermanager.events.OrderCreatedEvent;
import com.example.ecommerce.ordermanager.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${spring.kafka.topics.produce.order.created}")
    private String orderCreatedTopic;

    private final OrderRepository repository;
    private final KafkaTemplate kafkaTemplate;

    public Order createOrder(Order order) {
        Order savedOrder = repository.save(order);

        Message<OrderCreatedEvent> message = MessageBuilder.withPayload(OrderCreatedEvent.builder()
                .items(order.getItems()).build())
                .setHeader(KafkaHeaders.TOPIC, orderCreatedTopic)
                .build();

        kafkaTemplate.send(message);
        return savedOrder;
    }

    public Order getOrder(int orderId) {
        return repository.findById(orderId).orElseThrow();
    }


}
