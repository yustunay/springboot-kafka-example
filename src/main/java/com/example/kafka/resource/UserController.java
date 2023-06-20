package com.example.kafka.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.kafka.domain.User;

@RestController
@RequestMapping("kafka")
public class UserController {
	
	@Value("${message.topic.name}")
	private String topic;
	
	@Autowired
	private KafkaTemplate<String, User> kafkaTemplate;
	
	@PostMapping(value = "/publish",consumes = "application/json")
	public ResponseEntity<?> publish(@RequestBody User user) {
		kafkaTemplate.send(topic, user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
