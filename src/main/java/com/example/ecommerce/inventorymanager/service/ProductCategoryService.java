package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.ProductCategory;
import com.example.ecommerce.inventorymanager.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository repository;

    public ProductCategory createProductCategory(ProductCategory productCategory) {
        return repository.save(productCategory);
    }

    public ProductCategory getProductCategory(int productId) {
        return repository.findById(productId)
                .orElseThrow();
    }
}
