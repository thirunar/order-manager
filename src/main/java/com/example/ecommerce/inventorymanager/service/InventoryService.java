package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

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
    }
}
