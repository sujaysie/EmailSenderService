package com.example.emailsender.template.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class TemplateDtos {
    private TemplateDtos() { }

    public record CreateTemplateRequest(
            String name,
            String description,
            String category,
            String subject,
            String htmlBody,
            String textBody,
            Map<String, String> variables
    ) { }

    public record PublishVersionRequest(
            String subject,
            String htmlBody,
            String textBody,
            Map<String, String> variables,
            boolean activate
    ) { }

    public record TemplateResponse(
            UUID id,
            String name,
            String description,
            String category,
            Integer activeVersion,
            Instant updatedAt
    ) { }

    public record TemplateVersionResponse(
            UUID id,
            Integer versionNumber,
            String subject,
            String checksum,
            Instant createdAt
    ) { }

    public record RenderedTemplate(String subject, String htmlBody, String textBody) { }
}
