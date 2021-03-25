package com.example.ecommerce.inventorymanager.repository;

import com.example.ecommerce.inventorymanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
