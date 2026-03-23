package com.example.emailsender.template.repository;

import com.example.emailsender.template.entity.Template;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
    Optional<Template> findByIdAndDeletedFalse(UUID id);

    @Query("select t.activeVersion from Template t where t.id = :templateId and t.deleted = false")
    Optional<com.example.emailsender.template.entity.TemplateVersion> findActiveVersion(UUID templateId);
}
