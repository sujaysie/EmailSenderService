package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RetryHandler {
    private static final int MAX_RETRIES = 5;
    private final KafkaTemplate<String, EmailSendEvent> kafkaTemplate;

    public RetryHandler(KafkaTemplate<String, EmailSendEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void scheduleRetry(EmailSendEvent event, Exception cause, int currentAttempt) {
        if (currentAttempt >= Math.min(MAX_RETRIES, event.maxRetries())) {
            sendToDlq(event, cause);
            return;
        }
        int nextAttempt = currentAttempt + 1;
        long delayMs = (long) Math.pow(2, nextAttempt) * 1000L;
        RecordHeaders headers = new RecordHeaders();
        headers.add("x-retry-attempt", Integer.toString(nextAttempt).getBytes());
        headers.add("x-retry-after", Long.toString(System.currentTimeMillis() + delayMs).getBytes());
        kafkaTemplate.send(new ProducerRecord<>("email.send.retry", null, System.currentTimeMillis() + delayMs,
                event.eventId(), event, headers));
    }

    public void sendToDlq(EmailSendEvent event, Exception cause) {
        kafkaTemplate.send("email.send.dlq", event.eventId(), event);
    }
}
