package com.example.emailsender.template.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.emailsender.template.entity.TemplateVersion;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TemplateRenderEngineTest {
    private final TemplateRenderEngine engine = new TemplateRenderEngine();

    @Test
    void rendersSubjectHtmlAndDerivedText() {
        TemplateVersion version = new TemplateVersion();
        version.setSubject("Hello ${name}");
        version.setHtmlBody("<p>Welcome ${name}</p>");

        var rendered = engine.render(version, Map.of("name", "Taylor"));

        assertThat(rendered.subject()).isEqualTo("Hello Taylor");
        assertThat(rendered.htmlBody()).isEqualTo("<p>Welcome Taylor</p>");
        assertThat(rendered.textBody()).isEqualTo("Welcome Taylor");
    }
}
