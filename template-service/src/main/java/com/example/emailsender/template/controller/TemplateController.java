package com.example.emailsender.template.controller;

import com.example.emailsender.template.dto.TemplateDtos.CreateTemplateRequest;
import com.example.emailsender.template.dto.TemplateDtos.PublishVersionRequest;
import com.example.emailsender.template.dto.TemplateDtos.RenderedTemplate;
import com.example.emailsender.template.dto.TemplateDtos.TemplateResponse;
import com.example.emailsender.template.dto.TemplateDtos.TemplateVersionResponse;
import com.example.emailsender.template.service.TemplateService;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@RequestBody CreateTemplateRequest request) {
        return ResponseEntity.ok(templateService.createTemplate(request));
    }

    @PostMapping("/{templateId}/versions")
    public ResponseEntity<TemplateVersionResponse> publishVersion(
            @PathVariable UUID templateId,
            @RequestBody PublishVersionRequest request
    ) {
        return ResponseEntity.ok(templateService.publishVersion(templateId, request));
    }

    @GetMapping("/{templateId}/render")
    public ResponseEntity<RenderedTemplate> renderTemplate(
            @PathVariable UUID templateId,
            @RequestParam Map<String, Object> variables
    ) {
        return ResponseEntity.ok(templateService.renderTemplate(templateId, variables));
    }
}
