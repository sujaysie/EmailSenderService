package com.example.emailsender.gateway.client;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class InMemoryTemplateServiceClient implements TemplateServiceClient {
    @Override
    public void validateVariables(String templateName, Map<String, Object> variables) {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("templateName must be provided");
        }
        if (variables == null) {
            return;
        }
        variables.forEach((key, value) -> {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("Template variables must use non-blank keys");
            }
        });
    }
}
