package com.example.emailsender.engine.service;

import com.example.emailsender.shared.event.EmailSendEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.springframework.stereotype.Service;

@Service
public class MimeMessageBuilderService {
    public MimeMessage build(EmailSendEvent event, RenderedTemplate renderedTemplate) {
        try {
            MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
            message.setSubject(renderedTemplate.subject());
            if (!event.to().isEmpty()) {
                message.setRecipients(jakarta.mail.Message.RecipientType.TO,
                        event.to().stream().map(recipient -> {
                            try {
                                return new InternetAddress(recipient.email(), recipient.name());
                            } catch (Exception e) {
                                throw new IllegalArgumentException(e);
                            }
                        }).toArray(InternetAddress[]::new));
            }
            message.setContent(renderedTemplate.htmlBody(), "text/html; charset=UTF-8");
            return message;
        } catch (MessagingException e) {
            throw new IllegalStateException("Unable to build MIME message", e);
        }
    }
}
