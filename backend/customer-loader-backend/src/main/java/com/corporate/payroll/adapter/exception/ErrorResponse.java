package com.corporate.payroll.adapter.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Estructura estándar para respuestas de error en la API REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    private String message;
    
    private String errorCode;

    private long timestamp;
}
