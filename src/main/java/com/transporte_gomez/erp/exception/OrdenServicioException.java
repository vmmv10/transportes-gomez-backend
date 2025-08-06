package com.transporte_gomez.erp.exception;

public class OrdenServicioException extends RuntimeException{
    public OrdenServicioException(String message) {
        super(message);
    }

    public OrdenServicioException(String message, Throwable cause) {
        super(message, cause);
    }
}
