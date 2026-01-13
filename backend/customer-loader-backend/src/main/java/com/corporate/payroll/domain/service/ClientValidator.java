package com.corporate.payroll.domain.service;

import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Validador de clientes que devuelve errores en lugar de lanzar excepciones
 */
@Slf4j
public class ClientValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern ID_TYPE_PATTERN = Pattern.compile("^[CP]$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd", java.util.Locale.ROOT);

    /**
     * Sanitiza entrada del usuario para prevenir XSS
     */
    private static String sanitizeInput(String input) {
        if (input == null) return "null";
        return input.replaceAll("[<>\"'&]", "_");
    }

    /**
     * Valida un cliente completo y devuelve lista de TODOS los errores encontrados
     */
    public static List<BulkLoadError> validateClient(String idType, String idNumber, String joinDate,
                                                      String payrollValue, String email, String phoneNumber,
                                                      Integer lineNumber) {
        log.debug("Iniciando validación de cliente en fila: {}", lineNumber);
        List<BulkLoadError> errors = new ArrayList<>();

        // Validar Tipo de identificación
        if (idType == null || idType.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Tipo de identificación", "Campo 'Tipo de identificación' (columna 1): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validatePattern(idType, ID_TYPE_PATTERN,
                    "Campo 'Tipo de identificación' (columna 1): Debe ser 'C' (Cédula) o 'P' (Pasaporte). Valor encontrado: '" + sanitizeInput(idType) + "'", lineNumber);
            if (error != null) errors.add(error);
        }

        // Validar Número de identificación
        if (idNumber == null || idNumber.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Número de identificación", "Campo 'Número de identificación' (columna 2): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validatePattern(idNumber, ALPHANUMERIC_PATTERN,
                    "Campo 'Número de identificación' (columna 2): Debe ser alfanumérico. Valor encontrado: '" + sanitizeInput(idNumber) + "'", lineNumber);
            if (error != null) errors.add(error);
        }

        // Validar Fecha de ingreso
        if (joinDate == null || joinDate.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Fecha de ingreso", "Campo 'Fecha de ingreso' (columna 3): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validateDate(joinDate, DATE_FORMATTER,
                    "Campo 'Fecha de ingreso' (columna 3): Debe estar en formato yyyy-MM-dd. Valor encontrado: '" + sanitizeInput(joinDate) + "'", lineNumber);
            if (error != null) errors.add(error);
        }

        // Validar Valor del pago de nómina
        if (payrollValue == null || payrollValue.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Valor del pago de nómina", "Campo 'Valor del pago de nómina' (columna 4): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validateNumeric(payrollValue, "Campo 'Valor del pago de nómina' (columna 4)", lineNumber);
            if (error != null) {
                error.setErrorMessage("Campo 'Valor del pago de nómina' (columna 4): Debe contener únicamente valores numéricos. Valor encontrado: '" + sanitizeInput(payrollValue) + "'");
                errors.add(error);
            }
        }

        // Validar Correo electrónico
        if (email == null || email.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Correo electrónico", "Campo 'Correo electrónico' (columna 5): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validatePattern(email, EMAIL_PATTERN,
                    "Campo 'Correo electrónico' (columna 5): No tiene un formato válido. Valor encontrado: '" + sanitizeInput(email) + "'", lineNumber);
            if (error != null) errors.add(error);
        }

        // Validar Número de celular
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errors.add(ValidationUtils.createFieldError("Número de celular", "Campo 'Número de celular' (columna 6): El campo es requerido", lineNumber));
        } else {
            BulkLoadError error = ValidationUtils.validatePattern(phoneNumber, Pattern.compile("^\\d{10}$"),
                    "Campo 'Número de celular' (columna 6): Debe contener exactamente 10 dígitos numéricos. Valor encontrado: '" + sanitizeInput(phoneNumber) + "'", lineNumber);
            if (error != null) errors.add(error);
        }

        List<BulkLoadError> filteredErrors = errors.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        if (filteredErrors.isEmpty()) {
            log.debug("Validación exitosa para cliente en fila: {}", lineNumber);
        } else {
            log.warn("Se encontraron {} errores para cliente en fila: {}", 
                filteredErrors.size(), lineNumber);
        }
        
        return filteredErrors;
    }
}