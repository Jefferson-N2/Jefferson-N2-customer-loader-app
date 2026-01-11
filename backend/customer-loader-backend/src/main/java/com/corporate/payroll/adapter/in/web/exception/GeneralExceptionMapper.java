package com.corporate.payroll.adapter.in.web.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapea excepciones no controladas a respuestas HTTP.
 * Actúa como manejador global para errores inesperados.
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .message("Error interno del servidor: " + exception.getMessage())
            .errorCode("INTERNAL_SERVER_ERROR")
            .timestamp(System.currentTimeMillis())
            .build();
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(errorResponse)
            .build();
    }
}
