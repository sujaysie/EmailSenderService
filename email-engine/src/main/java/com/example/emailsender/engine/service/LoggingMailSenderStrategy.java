package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

@Component
public class LoggingMailSenderStrategy implements MailSenderStrategy {
    @Override
    public void send(MimeMessage message) {
        // placeholder sender for the scaffold implementation
    }

    @Override
    public boolean supports(EmailSendEvent.Priority priority) {
        return true;
    }
}
