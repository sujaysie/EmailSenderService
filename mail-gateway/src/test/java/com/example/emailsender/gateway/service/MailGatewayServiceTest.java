package com.example.emailsender.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.emailsender.gateway.client.TemplateServiceClient;
import com.example.emailsender.gateway.dto.MailGatewayDtos.SendMailRequest;
import com.example.emailsender.gateway.repository.OutboxRepository;
import com.example.emailsender.shared.event.EmailSendEvent.Priority;
import com.example.emailsender.shared.event.EmailSendEvent.Recipient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

class MailGatewayServiceTest {
    @Test
    void sendPersistsOutboxMessage() {
        KafkaTemplate<String, com.example.emailsender.shared.event.EmailSendEvent> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
        TemplateServiceClient templateServiceClient = org.mockito.Mockito.mock(TemplateServiceClient.class);
        OutboxRepository outboxRepository = org.mockito.Mockito.mock(OutboxRepository.class);
        when(outboxRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        MailGatewayService service = new MailGatewayService(kafkaTemplate, templateServiceClient, outboxRepository, new ObjectMapper());

        var response = service.send(new SendMailRequest(
                "welcome", null, List.of(new Recipient("a@example.com", "A")), List.of(), List.of(), null,
                Map.of("name", "A"), Map.of(), Priority.NORMAL, null, 3, "test"), "corr-1");

        assertThat(response.status()).isEqualTo("QUEUED");
        ArgumentCaptor<com.example.emailsender.gateway.entity.OutboxMessage> captor = ArgumentCaptor.forClass(com.example.emailsender.gateway.entity.OutboxMessage.class);
        verify(outboxRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(response.eventId());
    }
}
