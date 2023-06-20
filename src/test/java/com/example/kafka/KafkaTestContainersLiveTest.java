package com.example.kafka;

import com.example.kafka.domain.User;
import com.example.kafka.service.KafkaConsumer;
import com.example.kafka.service.KafkaProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@DirtiesContext
public class KafkaTestContainersLiveTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaConsumer consumer;
    @Autowired
    private KafkaProducer producer;

    @Value("${message.topic.name}")
    private String topic;

    @Test
    public void givenKafkaDockerContainer_whenSendingWithSimpleProducer_thenMessageReceived() throws Exception {
        User user = User.builder().message("Sample Message!").name("Deniz").build();
        producer.send(topic, user);
        boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertTrue(consumer.getPayload().equals(user));
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
        kafkaContainer.close();
    }

}