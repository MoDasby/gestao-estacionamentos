package com.modasby.gestaoestacionamentos.web.exception;

public class InvalidWebhookBodyException extends RuntimeException {
    public InvalidWebhookBodyException(String message) {
        super(message);
    }
}
