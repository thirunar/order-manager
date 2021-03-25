package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product getProduct(int productId) {
        return repository.findById(productId)
                .orElseThrow();
    }
}
