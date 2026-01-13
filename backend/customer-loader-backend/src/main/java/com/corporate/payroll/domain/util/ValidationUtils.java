package com.corporate.payroll.domain.util;

import com.corporate.payroll.domain.model.BulkLoadError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utilitario genérico para validaciones comunes.
 * Devuelve BulkLoadError en lugar de lanzar excepciones.
 */
public final class ValidationUtils {

    /**
     * Crea un error para un campo específico
     * @return BulkLoadError con el mensaje de error
     */
    public static BulkLoadError createFieldError(String fieldName, String errorMessage, Integer lineNumber) {
        return BulkLoadError.builder()
            .lineNumber(lineNumber)
            .errorMessage(errorMessage)
            .build();
    }

    /**
     * Valida que un valor no esté vacío
     * @return BulkLoadError si falla la validación, null si es válido
     */
    public static BulkLoadError validateNotBlank(String value, String fieldName, Integer lineNumber) {
        if (value == null || value.trim().isEmpty()) {
            return BulkLoadError.builder()
                .lineNumber(lineNumber)
                .errorMessage(fieldName + " no puede estar vacío")
                .build();
        }
        return null;
    }

    /**
     * Valida que un valor coincida con un patrón
     * @return BulkLoadError si falla la validación, null si es válido
     */
    public static BulkLoadError validatePattern(String value, Pattern pattern, String errorMessage, Integer lineNumber) {
        if (Objects.isNull(value) || !pattern.matcher(value.trim()).matches()) {
            return BulkLoadError.builder()
                .lineNumber(lineNumber)
                .errorMessage(errorMessage)
                .build();
        }
        return null;
    }

    /**
     * Valida que un valor sea numérico
     * @return BulkLoadError si falla la validación, null si es válido
     */
    public static BulkLoadError validateNumeric(String value, String fieldName, Integer lineNumber) {
        try {
            Double.parseDouble(Objects.toString(value,"").trim());
            return null;
        } catch (NumberFormatException e) {
            return BulkLoadError.builder()
                .lineNumber(lineNumber)
                .errorMessage(fieldName + " debe contener únicamente valores numéricos")
                .build();
        }
    }

    /**
     * Valida que un valor sea una fecha válida y no sea futura
     * @return BulkLoadError si falla la validación, null si es válido
     */
    public static BulkLoadError validateDate(String value, DateTimeFormatter formatter, String errorMessage, Integer lineNumber) {
        try {
            LocalDate parsedDate = LocalDate.parse(Objects.toString(value,"").trim(), formatter);
            
            // Validar que la fecha no sea futura
            if (parsedDate.isAfter(LocalDate.now())) {
                return BulkLoadError.builder()
                    .lineNumber(lineNumber)
                    .errorMessage("Campo 'Fecha de ingreso' (columna 3): La fecha no puede ser futura. Valor encontrado: '" + value + "'")
                    .build();
            }
            
            return null;
        } catch (DateTimeParseException e) {
            return BulkLoadError.builder()
                .lineNumber(lineNumber)
                .errorMessage(errorMessage)
                .build();
        }
    }

    private ValidationUtils() {
    }
}
