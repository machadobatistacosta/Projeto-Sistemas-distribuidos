package com.dlqSupport.exception;

public class MensagemNotFoundException extends RuntimeException {
    public MensagemNotFoundException(String message) {
        super(message);
    }
}
