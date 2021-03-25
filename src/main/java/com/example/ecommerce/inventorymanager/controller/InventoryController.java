package com.example.ecommerce.inventorymanager.controller;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.service.InventoryService;
import com.example.ecommerce.inventorymanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/inventory")
    public Inventory createInventory(Inventory inventory) {
        return service.createInventory(inventory);
    }

    @GetMapping("/inventory/{id}")
    public Inventory getProduct(@PathVariable("id") int inventoryId) {
        return service.getInventory(inventoryId);
    }
}
