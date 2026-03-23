package com.example.emailsender.gateway.repository;

import com.example.emailsender.gateway.entity.OutboxMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findBySentFalseOrderByCreatedAtAsc(Pageable pageable);
}
