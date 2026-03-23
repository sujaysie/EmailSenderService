package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogger.class);

    public void log(EmailSendEvent event, String deliveryStatus) {
        LOGGER.info("eventId={}, correlationId={}, status={}", event.eventId(), event.correlationId(), deliveryStatus);
    }
}
