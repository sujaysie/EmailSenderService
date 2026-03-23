package com.example.emailsender.template.service;

import com.example.emailsender.template.dto.TemplateDtos.CreateTemplateRequest;
import com.example.emailsender.template.dto.TemplateDtos.PublishVersionRequest;
import com.example.emailsender.template.dto.TemplateDtos.RenderedTemplate;
import com.example.emailsender.template.dto.TemplateDtos.TemplateResponse;
import com.example.emailsender.template.dto.TemplateDtos.TemplateVersionResponse;
import com.example.emailsender.template.entity.Template;
import com.example.emailsender.template.entity.TemplateVersion;
import com.example.emailsender.template.exception.TemplateNotFoundException;
import com.example.emailsender.template.mapper.TemplateMapper;
import com.example.emailsender.template.repository.TemplateRepository;
import com.example.emailsender.template.repository.TemplateVersionRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository templateVersionRepository;
    private final TemplateRenderEngine renderEngine;

    public TemplateService(
            TemplateRepository templateRepository,
            TemplateVersionRepository templateVersionRepository,
            TemplateRenderEngine renderEngine
    ) {
        this.templateRepository = templateRepository;
        this.templateVersionRepository = templateVersionRepository;
        this.renderEngine = renderEngine;
    }

    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        Template template = new Template();
        template.setName(request.name());
        template.setDescription(request.description());
        template.setCategory(request.category());
        templateRepository.save(template);

        TemplateVersion version = createVersion(template, 1, request.subject(), request.htmlBody(), request.textBody(), request.variables());
        template.setActiveVersion(version);
        template.setUpdatedAt(Instant.now());
        templateRepository.save(template);
        return TemplateMapper.toResponse(template);
    }

    @CacheEvict(value = "template", key = "#templateId")
    public TemplateVersionResponse publishVersion(UUID templateId, PublishVersionRequest request) {
        Template template = templateRepository.findByIdAndDeletedFalse(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(templateId));
        int nextVersion = templateVersionRepository.maxVersionNumber(templateId).map(existing -> existing + 1).orElse(1);
        TemplateVersion version = createVersion(template, nextVersion, request.subject(), request.htmlBody(), request.textBody(), request.variables());
        if (request.activate()) {
            template.setActiveVersion(version);
            template.setUpdatedAt(Instant.now());
            templateRepository.save(template);
        }
        return TemplateMapper.toVersionResponse(version);
    }

    @Cacheable(value = "template", key = "#templateId")
    @Transactional(readOnly = true)
    public RenderedTemplate renderTemplate(UUID templateId, Map<String, Object> variables) {
        TemplateVersion version = templateRepository.findActiveVersion(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(templateId));
        return renderEngine.render(version, variables);
    }

    private TemplateVersion createVersion(Template template, int versionNumber, String subject, String htmlBody, String textBody,
                                          Map<String, String> variables) {
        TemplateVersion version = new TemplateVersion();
        version.setTemplate(template);
        version.setVersionNumber(versionNumber);
        version.setSubject(subject);
        version.setHtmlBody(htmlBody);
        version.setTextBody(textBody);
        version.setVariables(variables);
        version.setChecksum(sha256(htmlBody));
        return templateVersionRepository.save(version);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
