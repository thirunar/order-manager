package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.entity.ProductCategory;
import com.example.ecommerce.inventorymanager.repository.ProductCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProductCategoryService.class)
class ProductCategoryServiceTest {

    @Autowired
    private ProductCategoryService service;

    @MockBean
    private ProductCategoryRepository repository;

    @Test
    void shouldSaveTheProductCategory() {
        ProductCategory productCategory = ProductCategory.builder().build();

        service.createProductCategory(productCategory);

        verify(repository).save(productCategory);
    }

    @Test
    void shouldGetTheProductById() {
        ProductCategory productCategory = ProductCategory.builder().id(1).build();
        when(repository.findById(any())).thenReturn(Optional.of(productCategory));

        ProductCategory productFromRepo = service.getProductCategory(1);

        verify(repository).findById(1);
        assertThat(productFromRepo).isEqualTo(productCategory);
    }
}