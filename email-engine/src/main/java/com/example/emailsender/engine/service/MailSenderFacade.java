package com.example.emailsender.engine.service;

import com.example.emailsender.engine.exception.NoSenderStrategyException;
import com.example.emailsender.shared.event.EmailSendEvent;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MailSenderFacade {
    private final List<MailSenderStrategy> strategies;

    public MailSenderFacade(List<MailSenderStrategy> strategies) {
        this.strategies = strategies;
    }

    public void send(MimeMessage message, EmailSendEvent.Priority priority) {
        strategies.stream()
                .filter(strategy -> strategy.supports(priority))
                .findFirst()
                .orElseThrow(() -> new NoSenderStrategyException(priority))
                .send(message);
    }
}
