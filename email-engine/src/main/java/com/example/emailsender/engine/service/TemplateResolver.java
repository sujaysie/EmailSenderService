package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TemplateResolver {
    public RenderedTemplate resolve(String templateName, String templateVersion, Map<String, Object> variables) {
        String subject = "Rendered " + templateName + (templateVersion == null ? "" : (" v" + templateVersion));
        String body = "<p>Email payload for " + templateName + "</p><pre>" + variables + "</pre>";
        return new RenderedTemplate(subject, body, body.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim());
    }
}
