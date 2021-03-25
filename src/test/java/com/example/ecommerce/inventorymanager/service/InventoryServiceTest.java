package com.example.ecommerce.inventorymanager.service;

import com.example.ecommerce.inventorymanager.config.KafkaConfig;
import com.example.ecommerce.inventorymanager.entity.Inventory;
import com.example.ecommerce.inventorymanager.entity.Product;
import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.events.RunningOutOfStockEvent;
import com.example.ecommerce.inventorymanager.repository.InventoryRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        InventoryService.class, KafkaConfig.class
})
@EmbeddedKafka(topics = {
        "order_created_event_v1", "product_out_of_stock_event_v1"
})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
class InventoryServiceTest {

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;


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

    private Consumer consumer;

    @BeforeEach
    void setUp() throws Exception {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", kafkaBroker);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        DefaultKafkaConsumerFactory<Object, Object> consumerFactory = new DefaultKafkaConsumerFactory(props,
                new StringDeserializer(), new JsonDeserializer<>(RunningOutOfStockEvent.class, false));
        consumer = consumerFactory.createConsumer("testGroup", "suffix");
        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
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

    @Test
    void shouldPublishOutOfStockEventIfTheQuantityIsLessThanThreshold() {
        when(repository.findByProductId(any())).thenReturn(Inventory.builder().product(Product.builder().id(1).build()).inStock(5).build());

        inventoryService.updateInventory(OrderCreatedEvent.builder().productId(1).quantity(4).build());

        ConsumerRecord record = KafkaTestUtils.getSingleRecord(consumer, "product_out_of_stock_event_v1");

        RunningOutOfStockEvent value = ((RunningOutOfStockEvent) record.value());
        assertThat(value).isEqualTo(RunningOutOfStockEvent.builder().productId(1).currentStock(1).build());
    }

    @AfterEach
    void tearDown() throws Exception {
        consumer.close();
    }

}