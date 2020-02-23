package com.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.example.kafka.domain.User;

@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {
 
    private static final Logger LOG = LoggerFactory.getLogger("KafkaApp");
 
    @Value("${message.topic.name}")
    private String topicName;
 
    private final KafkaTemplate<String, User> kafkaTemplate;
 
    @Autowired
    public KafkaApplication(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
 
    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }
 
    @Override
    public void run(String... strings) {
        Message<User> message = MessageBuilder.withPayload(new User("Ege-Deniz","Merhabaaaa"))
								              .setHeader(KafkaHeaders.TOPIC, topicName)
								              .build();
    	
        kafkaTemplate.send(message);
        LOG.info("Published message to topic: {}.", topicName);
    }
 
    @KafkaListener(topics = "${message.topic.name}", groupId = "jcg-group")
    public void listen(User message) {
        LOG.info("Received message in JCG group: {}", message);
    }
 
}
