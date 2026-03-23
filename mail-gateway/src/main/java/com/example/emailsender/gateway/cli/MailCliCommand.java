package com.example.emailsender.gateway.cli;

import com.example.emailsender.gateway.dto.MailGatewayDtos.SendMailRequest;
import com.example.emailsender.gateway.service.MailGatewayService;
import com.example.emailsender.shared.event.EmailSendEvent;
import com.example.emailsender.shared.event.EmailSendEvent.Recipient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "mail", description = "Send email via CLI")
public class MailCliCommand implements Runnable {
    @Option(names = {"-t", "--template"}, required = true)
    private String templateName;

    @Option(names = {"-r", "--recipient"}, required = true, split = ",")
    private List<String> recipients;

    @Option(names = {"-v", "--variables"})
    private String variablesJson;

    @Option(names = {"-p", "--priority"}, defaultValue = "NORMAL")
    private EmailSendEvent.Priority priority;

    private final MailGatewayService mailGatewayService;
    private final ObjectMapper objectMapper;

    public MailCliCommand(MailGatewayService mailGatewayService, ObjectMapper objectMapper) {
        this.mailGatewayService = mailGatewayService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        Map<String, Object> variables = parseVariables();
        SendMailRequest request = new SendMailRequest(
                templateName,
                null,
                recipients.stream().map(email -> new Recipient(email, null)).toList(),
                List.of(),
                List.of(),
                null,
                variables,
                Collections.emptyMap(),
                priority,
                null,
                3,
                "cli");
        var response = mailGatewayService.send(request, UUID.randomUUID().toString());
        System.out.printf("Queued: eventId=%s%n", response.eventId());
    }

    private Map<String, Object> parseVariables() {
        if (variablesJson == null || variablesJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(variablesJson, new TypeReference<>() { });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse variables JSON", ex);
        }
    }
}
