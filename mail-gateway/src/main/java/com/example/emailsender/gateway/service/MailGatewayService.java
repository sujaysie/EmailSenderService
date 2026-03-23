package com.example.emailsender.gateway.service;

import com.example.emailsender.gateway.client.TemplateServiceClient;
import com.example.emailsender.gateway.dto.MailGatewayDtos.BatchSendRequest;
import com.example.emailsender.gateway.dto.MailGatewayDtos.BatchSendResponse;
import com.example.emailsender.gateway.dto.MailGatewayDtos.DeliveryStatusResponse;
import com.example.emailsender.gateway.dto.MailGatewayDtos.SendMailRequest;
import com.example.emailsender.gateway.dto.MailGatewayDtos.SendResponse;
import com.example.emailsender.gateway.entity.OutboxMessage;
import com.example.emailsender.gateway.repository.OutboxRepository;
import com.example.emailsender.shared.event.EmailSendEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MailGatewayService {
    private final KafkaTemplate<String, EmailSendEvent> kafkaTemplate;
    private final TemplateServiceClient templateServiceClient;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public MailGatewayService(
            KafkaTemplate<String, EmailSendEvent> kafkaTemplate,
            TemplateServiceClient templateServiceClient,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.templateServiceClient = templateServiceClient;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SendResponse send(SendMailRequest request, String correlationId) {
        templateServiceClient.validateVariables(request.templateName(), request.variables());
        EmailSendEvent event = buildEvent(request, correlationId);
        outboxRepository.save(new OutboxMessage(event.eventId(), serialize(event)));
        return new SendResponse(event.eventId(), "QUEUED");
    }

    @Transactional
    public BatchSendResponse sendBatch(BatchSendRequest request, String correlationId) {
        return new BatchSendResponse(request.requests().stream().map(item -> send(item, correlationId)).toList());
    }

    public DeliveryStatusResponse getStatus(String eventId) {
        boolean queued = outboxRepository.findAll().stream().anyMatch(item -> item.getEventId().equals(eventId) && !item.isSent());
        return new DeliveryStatusResponse(eventId, queued ? "QUEUED" : "UNKNOWN");
    }

    @Scheduled(fixedDelay = 500)
    public void relayOutbox() {
        List<OutboxMessage> pending = outboxRepository.findBySentFalseOrderByCreatedAtAsc(PageRequest.of(0, 100));
        pending.forEach(message -> {
            kafkaTemplate.send("email.send.requests", message.getEventId(), deserialize(message.getPayload()));
            message.setSent(true);
            outboxRepository.save(message);
        });
    }

    EmailSendEvent buildEvent(SendMailRequest request, String correlationId) {
        return new EmailSendEvent(
                UUID.randomUUID().toString(),
                correlationId,
                request.templateName(),
                request.templateVersion(),
                request.to(),
                request.cc(),
                request.bcc(),
                request.replyTo(),
                request.variables(),
                request.headers(),
                request.priority() == null ? EmailSendEvent.Priority.NORMAL : request.priority(),
                request.scheduledAt(),
                request.maxRetries() == null ? 3 : request.maxRetries(),
                request.sourceApp() == null ? "mail-gateway" : request.sourceApp());
    }

    private String serialize(EmailSendEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize event", e);
        }
    }

    private EmailSendEvent deserialize(String payload) {
        try {
            return objectMapper.readValue(payload, EmailSendEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize event", e);
        }
    }
}
