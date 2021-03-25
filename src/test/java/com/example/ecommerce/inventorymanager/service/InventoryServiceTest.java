package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InventoryService.class)
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @MockBean
    private InventoryRepository repository;

    @Test
    void shouldAddAnInventoryItem() {
        Inventory inventory = Inventory.builder().product(Product.builder().build()).inStock(10).build();

        inventoryService.createInventory(inventory);

        verify(repository).save(inventory);
    }

    @Test
    void shouldFetchTheInventoryAndThenUpdateTheStock() {
        when(repository.findByProductId(any())).thenReturn(Inventory.builder().product(Product.builder().id(1).build()).inStock(10).build());

        inventoryService.updateInventory(OrderCreatedEvent.builder().productId(1).quantity(2).build());

        verify(repository).findByProductId(1);
        verify(repository).save(argThat(inventory -> {
            assertThat(inventory.getInStock()).isEqualTo(8);
            return true;
        }));
    }
}