package com.example.emailsender.engine.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic emailSendRequests() {
        return TopicBuilder.name("email.send.requests")
                .partitions(12)
                .replicas(3)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "lz4")
                .build();
    }

    @Bean
    public NewTopic emailSendRetry() {
        return TopicBuilder.name("email.send.retry")
                .partitions(6)
                .replicas(3)
                .config(TopicConfig.RETENTION_MS_CONFIG, "86400000")
                .build();
    }

    @Bean
    public NewTopic emailSendDlq() {
        return TopicBuilder.name("email.send.dlq")
                .partitions(3)
                .replicas(3)
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000")
                .build();
    }

    @Bean
    public NewTopic emailDeliveryStatus() {
        return TopicBuilder.name("email.delivery.status")
                .partitions(6)
                .replicas(3)
                .build();
    }
}
