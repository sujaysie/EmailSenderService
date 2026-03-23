package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import jakarta.mail.internet.MimeMessage;

public interface MailSenderStrategy {
    void send(MimeMessage message);
    boolean supports(EmailSendEvent.Priority priority);
}
