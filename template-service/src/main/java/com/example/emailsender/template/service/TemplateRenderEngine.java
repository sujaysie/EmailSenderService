package com.example.emailsender.template.service;

import com.example.emailsender.template.dto.TemplateDtos.RenderedTemplate;
import com.example.emailsender.template.entity.TemplateVersion;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderEngine {
    public RenderedTemplate render(TemplateVersion version, Map<String, Object> variables) {
        StringSubstitutor substitutor = new StringSubstitutor(variables, "${", "}");
        String renderedSubject = substitutor.replace(version.getSubject());
        String renderedHtml = substitutor.replace(version.getHtmlBody());
        String renderedText = version.getTextBody() != null
                ? substitutor.replace(version.getTextBody())
                : renderedHtml.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        return new RenderedTemplate(renderedSubject, renderedHtml, renderedText);
    }
}
