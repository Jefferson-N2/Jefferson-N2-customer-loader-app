package com.corporate.payroll.adapter.exception;

import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.Map;

/**
 * Mapea excepciones de lógica de negocio a respuestas HTTP.
 * Determina el código de error y estado HTTP basado en el mensaje de la excepción.
 */
@Provider
public class BusinessLogicExceptionMapper implements ExceptionMapper<BusinessLogicException> {

    private static final Map<String, ErrorMapping> ERROR_MAPPINGS = Map.of(
            "no existe", new ErrorMapping("NOT_FOUND", Response.Status.NOT_FOUND),
            "invalid", new ErrorMapping("INVALID_DATA", Response.Status.CONFLICT),
            "inválido", new ErrorMapping("INVALID_DATA", Response.Status.CONFLICT),
            "missing", new ErrorMapping("MISSING_PARAMETER", Response.Status.CONFLICT),
            "requerido", new ErrorMapping("MISSING_PARAMETER", Response.Status.CONFLICT),
            "conflict", new ErrorMapping("DUPLICATE_CLIENT", Response.Status.CONFLICT),
            "duplicated", new ErrorMapping("DUPLICATE_CLIENT", Response.Status.CONFLICT)
    );

    @Override
    public Response toResponse(BusinessLogicException exception) {
        String message = exception.getMessage() != null ?
                exception.getMessage().toLowerCase() : "";

        ErrorMapping mapping = ERROR_MAPPINGS.entrySet().stream()
                .filter(entry -> message.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new ErrorMapping("BUSINESS_LOGIC_ERROR", Response.Status.BAD_REQUEST));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .errorCode(mapping.errorCode())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        return Response.status(mapping.status())
                .entity(errorResponse)
                .build();
    }

    private record ErrorMapping(String errorCode, Response.Status status) {}
}
