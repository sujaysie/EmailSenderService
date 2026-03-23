package com.example.emailsender.engine.consumer;

import com.example.emailsender.engine.exception.PermanentMailException;
import com.example.emailsender.engine.exception.TemporaryMailException;
import com.example.emailsender.engine.service.AuditLogger;
import com.example.emailsender.engine.service.IdempotencyFilter;
import com.example.emailsender.engine.service.MailSenderFacade;
import com.example.emailsender.engine.service.MimeMessageBuilderService;
import com.example.emailsender.engine.service.RenderedTemplate;
import com.example.emailsender.engine.service.RetryHandler;
import com.example.emailsender.engine.service.StatusPublisher;
import com.example.emailsender.engine.service.TemplateResolver;
import com.example.emailsender.shared.event.EmailSendEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class EmailSendRequestConsumer {
    private final IdempotencyFilter idempotencyFilter;
    private final TemplateResolver templateResolver;
    private final MimeMessageBuilderService mimeMessageBuilderService;
    private final MailSenderFacade mailSenderFacade;
    private final StatusPublisher statusPublisher;
    private final AuditLogger auditLogger;
    private final RetryHandler retryHandler;

    public EmailSendRequestConsumer(IdempotencyFilter idempotencyFilter,
                                    TemplateResolver templateResolver,
                                    MimeMessageBuilderService mimeMessageBuilderService,
                                    MailSenderFacade mailSenderFacade,
                                    StatusPublisher statusPublisher,
                                    AuditLogger auditLogger,
                                    RetryHandler retryHandler) {
        this.idempotencyFilter = idempotencyFilter;
        this.templateResolver = templateResolver;
        this.mimeMessageBuilderService = mimeMessageBuilderService;
        this.mailSenderFacade = mailSenderFacade;
        this.statusPublisher = statusPublisher;
        this.auditLogger = auditLogger;
        this.retryHandler = retryHandler;
    }

    @KafkaListener(topics = "email.send.requests", groupId = "email-engine-group", concurrency = "5",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, EmailSendEvent> record, Acknowledgment acknowledgment) {
        EmailSendEvent event = record.value();
        try {
            if (idempotencyFilter.isDuplicate(event.eventId())) {
                acknowledgment.acknowledge();
                return;
            }

            RenderedTemplate renderedTemplate = templateResolver.resolve(event.templateName(), event.templateVersion(), event.variables());
            var message = mimeMessageBuilderService.build(event, renderedTemplate);
            mailSenderFacade.send(message, event.priority());
            statusPublisher.publishSuccess(event);
            auditLogger.log(event, "DELIVERED");
            acknowledgment.acknowledge();
        } catch (TemporaryMailException ex) {
            retryHandler.scheduleRetry(event, ex, 0);
        } catch (PermanentMailException ex) {
            retryHandler.sendToDlq(event, ex);
            auditLogger.log(event, "FAILED_PERMANENT");
            acknowledgment.acknowledge();
        }
    }
}
