package com.modasby.gestaoestacionamentos.web.exception;

public class GarageFullCapacityException extends RuntimeException {
    public GarageFullCapacityException(String message) {
        super(message);
    }
}
