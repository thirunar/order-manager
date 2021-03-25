package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.events.RunningOutOfStockEvent;
import com.example.ecommerce.inventorymanager.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Value("${inventory.threshold.out-of-stock:3}")
    private Integer threshold;

    @Value("${spring.kafka.topics.produce.inventory.out-of-stock}")
    private String outOfStockTopic;

    private final InventoryRepository repository;
    private final KafkaTemplate kafkaTemplate;

    public Inventory createInventory(Inventory inventory) {
        return repository.save(inventory);
    }

    public Inventory getInventory(int inventoryId) {
        return repository.findById(inventoryId).orElseThrow();
    }

    public void updateInventory(OrderCreatedEvent orderCreatedEvent) {
        Inventory inventory = repository.findByProductId(orderCreatedEvent.getProductId());

        inventory.setInStock(inventory.getInStock() - orderCreatedEvent.getQuantity());

        repository.save(inventory);

        if (threshold >= inventory.getInStock()) {
            Message<RunningOutOfStockEvent> message = MessageBuilder.withPayload(RunningOutOfStockEvent.builder()
                    .productId(inventory.getProduct().getId())
                    .currentStock(inventory.getInStock()).build())
                    .setHeader(KafkaHeaders.TOPIC, outOfStockTopic)
                    .build();
            kafkaTemplate.send(message);
        }
    }
}
