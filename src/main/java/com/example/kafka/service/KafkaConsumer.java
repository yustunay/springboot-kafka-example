package com.example.kafka.service;

import com.example.kafka.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Component
public class KafkaConsumer {
    private static final Logger LOG = LoggerFactory.getLogger("KafkaConsumer");

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private CountDownLatch latch = new CountDownLatch(1);
    private User payload;

    @KafkaListener(topics = "${message.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(User message) {
        LOG.info("Received message in {} group: {}", groupId, message);
        payload = message;
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public User getPayload() {
        return payload;
    }
}
