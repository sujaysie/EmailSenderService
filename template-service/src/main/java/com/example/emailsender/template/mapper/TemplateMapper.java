package com.example.emailsender.template.mapper;

import com.example.emailsender.template.dto.TemplateDtos.TemplateResponse;
import com.example.emailsender.template.dto.TemplateDtos.TemplateVersionResponse;
import com.example.emailsender.template.entity.Template;
import com.example.emailsender.template.entity.TemplateVersion;

public final class TemplateMapper {
    private TemplateMapper() { }

    public static TemplateResponse toResponse(Template template) {
        Integer activeVersion = template.getActiveVersion() == null ? null : template.getActiveVersion().getVersionNumber();
        return new TemplateResponse(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.getCategory(),
                activeVersion,
                template.getUpdatedAt());
    }

    public static TemplateVersionResponse toVersionResponse(TemplateVersion version) {
        return new TemplateVersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getSubject(),
                version.getChecksum(),
                version.getCreatedAt());
    }
}
