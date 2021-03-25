package com.example.ecommerce.inventorymanager.repository;

import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.entity.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Test
    void shouldReturnInventoryBasedOnProductId() {
        ProductCategory category = categoryRepository.save(ProductCategory.builder().id(1).name("Mobile").build());
        Product product = productRepository.save(Product.builder().category(category).build());
        Inventory inventory = repository.save(Inventory.builder().product(product).inStock(10).build());

        Inventory actualInventory = repository.findByProductId(product.getId());

        assertThat(actualInventory).isEqualTo(inventory);
    }
}