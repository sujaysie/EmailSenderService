package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatusPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public StatusPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSuccess(EmailSendEvent event) {
        kafkaTemplate.send("email.delivery.status", event.eventId(), "DELIVERED");
    }
}
