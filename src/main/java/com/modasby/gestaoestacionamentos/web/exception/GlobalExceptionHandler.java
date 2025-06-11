package com.modasby.gestaoestacionamentos.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(GarageFullCapacityException.class)
    public ResponseEntity<ExceptionBody> handleGarageFullCapacityException(GarageFullCapacityException e) {
        logger.error(e.getMessage());

        return ResponseEntity.badRequest().body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(SpotNotAvailableException.class)
    public ResponseEntity<ExceptionBody> handleSpotNotAvailableException(SpotNotAvailableException e) {
        logger.error(e.getMessage());

        return ResponseEntity.badRequest().body(new ExceptionBody(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionBody> handleEntityNotFoundException(EntityNotFoundException e) {
        logger.error(e.getMessage());

        return new ResponseEntity<>(new ExceptionBody(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidWebhookBodyException.class)
    public ResponseEntity<ExceptionBody> handleInvalidWebhookBodyException(InvalidWebhookBodyException e) {
        logger.error(e.getMessage());

        return ResponseEntity.badRequest().body(new ExceptionBody(e.getMessage()));
    }
}
