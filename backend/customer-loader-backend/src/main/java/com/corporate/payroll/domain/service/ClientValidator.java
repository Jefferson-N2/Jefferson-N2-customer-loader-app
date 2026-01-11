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

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ID_TYPE_PATTERN = Pattern.compile("^[CP]$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Valida un cliente completo y devuelve lista de TODOS los errores encontrados
     */
    public static List<BulkLoadError> validateClient(String idType, String idNumber, String joinDate,
                                                      String payrollValue, String email, String phoneNumber,
                                                      Integer lineNumber) {
        log.debug("Iniciando validación de cliente en fila: {}", lineNumber);
        List<BulkLoadError> errors = new ArrayList<>();

        errors.add(ValidationUtils.validateNotBlank(idType, "Tipo de identificación", lineNumber));
        errors.add(ValidationUtils.validatePattern(idType, ID_TYPE_PATTERN,
                "Tipo de identificación debe ser 'C' (Cédula) o 'P' (Pasaporte)", lineNumber));

        errors.add(ValidationUtils.validateNotBlank(idNumber, "Número de identificación", lineNumber));
        errors.add(ValidationUtils.validatePattern(idNumber, ALPHANUMERIC_PATTERN,
                "Número de identificación debe ser alfanumérico", lineNumber));

        errors.add(ValidationUtils.validateNotBlank(joinDate, "Fecha de ingreso", lineNumber));
        errors.add(ValidationUtils.validateDate(joinDate, DATE_FORMATTER,
                "Fecha de ingreso debe estar en formato yyyy-MM-dd", lineNumber));

        errors.add(ValidationUtils.validateNotBlank(payrollValue, "Valor del pago de nómina", lineNumber));
        errors.add(ValidationUtils.validateNumeric(payrollValue, "Valor del pago de nómina", lineNumber));

        errors.add(ValidationUtils.validateNotBlank(email, "Correo electrónico", lineNumber));
        errors.add(ValidationUtils.validatePattern(email, EMAIL_PATTERN,
                "Correo electrónico no tiene un formato válido", lineNumber));

        errors.add(ValidationUtils.validateNotBlank(phoneNumber, "Número de celular", lineNumber));
        errors.add(ValidationUtils.validatePattern(phoneNumber, Pattern.compile("^\\d{10}$"),
                "Número de celular debe contener exactamente 10 dígitos numéricos", lineNumber));

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