package com.example.kafka;


import com.example.kafka.domain.User;
import com.example.kafka.service.KafkaConsumer;
import com.example.kafka.service.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class EmbeddedKafkaIntegrationTest {
    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    @BeforeEach
    void setup() {
        consumer.resetLatch();
    }

    @Value("${message.topic.name}")
    private String topic;

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithDefaultTemplate_thenMessageReceived()
      throws Exception {
        String data = "Sending with default template";
        User user = new User("Ege", data);

        kafkaTemplate.send(topic, user);
        
        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getPayload().getName(), is("Ege"));
        assertThat(consumer.getPayload().getMessage(), is(data));
    }

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() throws Exception {
        String data = "Sending with our own simple KafkaProducer";
        User user = new User("Deniz", data);

        producer.send(topic, user);

        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getPayload().getName(), is("Deniz"));
        assertThat(consumer.getPayload().getMessage(), is(data));
    }
}