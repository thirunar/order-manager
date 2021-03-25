package com.example.ecommerce.inventorymanager.repository;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findByProductId(Integer productId);
}
