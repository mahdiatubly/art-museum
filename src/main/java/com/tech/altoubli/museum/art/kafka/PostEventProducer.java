package com.tech.altoubli.museum.art.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostEventProducer {

    private static final String TOPIC = "feed.posts";

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    public void sendPostEvent(Long postId) {
        kafkaTemplate.send(TOPIC, postId);
    }
}
