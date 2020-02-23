package com.example.kafka.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.domain.User;

@RestController
@RequestMapping("kafka")
public class UserController {
	
	@Value("${message.topic.name}")
	private String topic;
	
	@Autowired
	private KafkaTemplate<String, User> kafkaTemplate;
	
	@GetMapping("/publish/{message}")
	public String publish(@PathVariable String message) {
		kafkaTemplate.send(topic, new User("Yahya",message));
		return "Published successfully!";
	}
	
}
