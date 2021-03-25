package com.example.ecommerce.ordermanager.service;

import com.example.ecommerce.ordermanager.config.KafkaConfig;
import com.example.ecommerce.ordermanager.entity.Order;
import com.example.ecommerce.ordermanager.entity.OrderItem;
import com.example.ecommerce.ordermanager.events.OrderCreatedEvent;
import com.example.ecommerce.ordermanager.repository.OrderRepository;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        OrderService.class, KafkaConfig.class
})
@EmbeddedKafka(topics = {
        "order_created_event_v1"
})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
class OrderServiceTest {

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository repository;

    private Consumer consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", kafkaBroker);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        DefaultKafkaConsumerFactory<Object, Object> consumerFactory = new DefaultKafkaConsumerFactory(props,
                new StringDeserializer(), new JsonDeserializer<>(OrderCreatedEvent.class, false));
        consumer = consumerFactory.createConsumer("testGroup", "suffix");
        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @Test
    void shouldCreateAnOrder() {
        Order order = Order.builder().id(1).item(OrderItem.builder().productId(1).quantity(10).build()).build();

        orderService.createOrder(order);

        verify(repository).save(order);
    }

    @Test
    void shouldPublishOrderCreated() {
        Order order = Order.builder().item(OrderItem.builder().productId(1).quantity(4).build()).build();
        when(repository.save(any())).thenReturn(order);

        orderService.createOrder(order);

        ConsumerRecord record = KafkaTestUtils.getSingleRecord(consumer, "order_created_event_v1");

        OrderCreatedEvent value = ((OrderCreatedEvent) record.value());
        assertThat(value).isEqualTo(OrderCreatedEvent.builder().item(OrderItem.builder().productId(1).quantity(4).build()).build());
    }

    @AfterEach
    void tearDown() throws Exception {
        consumer.close();
    }

}