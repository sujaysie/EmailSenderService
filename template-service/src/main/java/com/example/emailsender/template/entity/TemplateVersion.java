package com.example.emailsender.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "template_versions")
public class TemplateVersion {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String htmlBody;

    @Lob
    private String textBody;

    @ElementCollection
    private Map<String, String> variables = new HashMap<>();

    private String checksum;
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public Template getTemplate() { return template; }
    public void setTemplate(Template template) { this.template = template; }
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getHtmlBody() { return htmlBody; }
    public void setHtmlBody(String htmlBody) { this.htmlBody = htmlBody; }
    public String getTextBody() { return textBody; }
    public void setTextBody(String textBody) { this.textBody = textBody; }
    public Map<String, String> getVariables() { return variables; }
    public void setVariables(Map<String, String> variables) { this.variables = variables; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public Instant getCreatedAt() { return createdAt; }
}
