package com.example.emailsender.shared.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailSendEvent(
        String eventId,
        String correlationId,
        String templateName,
        String templateVersion,
        List<Recipient> to,
        List<Recipient> cc,
        List<Recipient> bcc,
        Recipient replyTo,
        Map<String, Object> variables,
        Map<String, String> headers,
        Priority priority,
        Instant scheduledAt,
        int maxRetries,
        String sourceApp
) {
    public enum Priority { HIGH, NORMAL, LOW }

    public record Recipient(String email, String name) { }
}
