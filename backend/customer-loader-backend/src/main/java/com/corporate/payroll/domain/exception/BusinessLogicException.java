package com.corporate.payroll.domain.exception;

/**
 * Excepción para errores de lógica de negocio
 */
public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }
}
