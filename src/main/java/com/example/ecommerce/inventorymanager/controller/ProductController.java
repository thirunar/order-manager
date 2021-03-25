package com.example.ecommerce.inventorymanager.controller;

import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping("/product")
    public Product createProduct(Product product) {
        return service.createProduct(product);
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable("id") int productId) {
        return service.getProduct(productId);
    }
}
