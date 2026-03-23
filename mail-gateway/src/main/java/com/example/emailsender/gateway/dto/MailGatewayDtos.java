package com.example.emailsender.gateway.dto;

import com.example.emailsender.shared.event.EmailSendEvent.Priority;
import com.example.emailsender.shared.event.EmailSendEvent.Recipient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class MailGatewayDtos {
    private MailGatewayDtos() { }

    public record SendMailRequest(
            @NotBlank String templateName,
            String templateVersion,
            @Valid @NotEmpty List<Recipient> to,
            List<Recipient> cc,
            List<Recipient> bcc,
            Recipient replyTo,
            Map<String, Object> variables,
            Map<String, String> headers,
            Priority priority,
            Instant scheduledAt,
            Integer maxRetries,
            String sourceApp
    ) { }

    public record BatchSendRequest(@Valid @Size(max = 500) List<SendMailRequest> requests) { }
    public record SendResponse(String eventId, String status) { }
    public record BatchSendResponse(List<SendResponse> responses) { }
    public record DeliveryStatusResponse(String eventId, String status) { }
}
