package com.example.emailsender.engine.exception;

import com.example.emailsender.shared.event.EmailSendEvent;

public class NoSenderStrategyException extends RuntimeException {
    public NoSenderStrategyException(EmailSendEvent.Priority priority) {
        super("No sender strategy configured for priority: " + priority);
    }
}
