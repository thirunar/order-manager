package com.example.ecommerce.inventorymanager.listener;

import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryListener {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "${spring.kafka.topics.listen.order.created}")
    public void onOrderCreated(@Payload OrderCreatedEvent orderCreatedEvent) {
        inventoryService.updateInventory(orderCreatedEvent);
    }

}
