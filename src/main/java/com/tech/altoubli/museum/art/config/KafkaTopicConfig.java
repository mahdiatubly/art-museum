package com.tech.altoubli.museum.art.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic FeedPostsTopic() {
        return TopicBuilder
                .name("feed.posts")
                .build();
    }
}
