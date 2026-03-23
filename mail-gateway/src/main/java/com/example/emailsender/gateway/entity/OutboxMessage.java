package com.example.emailsender.gateway.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessage {
    @Id
    @GeneratedValue
    private Long id;
    private String eventId;
    @Lob
    private String payload;
    private boolean sent;
    private int retryCount;
    private Instant createdAt = Instant.now();

    public OutboxMessage() { }

    public OutboxMessage(String eventId, String payload) {
        this.eventId = eventId;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getPayload() { return payload; }
    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public Instant getCreatedAt() { return createdAt; }
}
