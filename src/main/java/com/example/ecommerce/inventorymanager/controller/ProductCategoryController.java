package com.example.ecommerce.inventorymanager.controller;

import com.example.ecommerce.inventorymanager.entity.ProductCategory;
import com.example.ecommerce.inventorymanager.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService service;

    @PostMapping("/product-category")
    public ProductCategory createProduct(ProductCategory productCategory) {
        return service.createProductCategory(productCategory);
    }

    @GetMapping("/product/{id}")
    public ProductCategory getProduct(@PathVariable("id") int productId) {
        return service.getProductCategory(productId);
    }
}
