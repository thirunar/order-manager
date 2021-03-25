package com.example.ecommerce.inventorymanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {
		"order_created_event_v1", "product_out_of_stock_event_v1"
})
@TestPropertySource(properties = {
		"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
class InventoryManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
