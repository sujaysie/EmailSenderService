package com.example.emailsender.template.repository;

import com.example.emailsender.template.entity.TemplateVersion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, UUID> {
    @Query("select max(tv.versionNumber) from TemplateVersion tv where tv.template.id = :templateId")
    Optional<Integer> maxVersionNumber(UUID templateId);
}
