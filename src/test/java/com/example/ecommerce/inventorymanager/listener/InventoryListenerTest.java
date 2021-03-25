package com.example.ecommerce.inventorymanager.listener;

import com.example.ecommerce.inventorymanager.config.KafkaConfig;
import com.example.ecommerce.inventorymanager.events.OrderCreatedEvent;
import com.example.ecommerce.inventorymanager.service.InventoryService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableKafka
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        InventoryListener.class, KafkaConfig.class
})
@EmbeddedKafka(topics = {
        "order_created_event_v1", "product_out_of_stock_event_v1"
})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
})
@MockBeans(value = {
        @MockBean(value = InventoryService.class)
})
class InventoryListenerTest {

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @SpyBean
    private InventoryListener listener;


    private Consumer consumer;

    @BeforeEach
    void setUp() throws Exception {

        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", kafkaBroker);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        DefaultKafkaConsumerFactory<Object, Object> consumerFactory = new DefaultKafkaConsumerFactory(props,
                new StringDeserializer(), new JsonDeserializer<>(Object.class, false));
        consumer = consumerFactory.createConsumer("testGroup", "suffix");
        kafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @Test
    void shouldInvokeTheListener() {
        OrderCreatedEvent orderCreated = OrderCreatedEvent.builder().productId(1).quantity(1).build();
        Message<OrderCreatedEvent> orderCreatedEvent = MessageBuilder.withPayload(orderCreated)
                .setHeader(KafkaHeaders.TOPIC, "order_created_event_v1")
                .build();

        kafkaTemplate.send(orderCreatedEvent);

        verify(listener, timeout(10000)).onOrderCreated(orderCreated);
    }
}