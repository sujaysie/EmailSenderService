package com.example.emailsender.gateway.client;

import java.util.Map;

public interface TemplateServiceClient {
    void validateVariables(String templateName, Map<String, Object> variables);
}
