package com.example.emailsender.engine.exception;

public class PermanentMailException extends RuntimeException {
    public PermanentMailException(String message) {
        super(message);
    }
}
