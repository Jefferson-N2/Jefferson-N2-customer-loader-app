package com.corporate.payroll.application.util;

import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.util.ValidationUtils;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    private static final Integer ROW_NUMBER = 1;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    void testValidateNotBlankWithValidValueReturnsNull() {
        BulkLoadError result = ValidationUtils.validateNotBlank("valid value", "Test Field", ROW_NUMBER);
        assertNull(result, "Valid value should return null");
    }

    @Test
    void testValidateNotBlankWithNullValueReturnsError() {
        BulkLoadError result = ValidationUtils.validateNotBlank(null, "Test Field", ROW_NUMBER);
        
        assertNotNull(result, "Null value should return error");
        assertTrue(result.getErrorMessage().contains("no puede estar vacío"), "Error message should contain 'no puede estar vacío'");
        assertEquals(ROW_NUMBER, result.getLineNumber(), "Line number should match");
    }

    @Test
    void testValidateNotBlankWithEmptyValueReturnsError() {
        BulkLoadError result = ValidationUtils.validateNotBlank("", "Test Field", ROW_NUMBER);
        
        assertNotNull(result, "Empty value should return error");
        assertTrue(result.getErrorMessage().contains("no puede estar vacío"), "Error message should contain 'no puede estar vacío'");
    }

    @Test
    void testValidateNotBlankWithBlankValueReturnsError() {
        BulkLoadError result = ValidationUtils.validateNotBlank("   ", "Test Field", ROW_NUMBER);
        
        assertNotNull(result, "Blank value should return error");
        assertTrue(result.getErrorMessage().contains("no puede estar vacío"), "Error message should contain 'no puede estar vacío'");
    }

    @Test
    void testValidatePatternWithValidEmailReturnsNull() {
        BulkLoadError result = ValidationUtils.validatePattern("test@email.com", EMAIL_PATTERN, "Email inválido", ROW_NUMBER);
        assertNull(result, "Valid email should return null");
    }

    @Test
    void testValidatePatternWithInvalidEmailReturnsError() {
        BulkLoadError result = ValidationUtils.validatePattern("invalid-email", EMAIL_PATTERN, "Email inválido", ROW_NUMBER);
        
        assertNotNull(result, "Invalid email should return error");
        assertEquals("Email inválido", result.getErrorMessage(), "Error message should match expected");
    }

    @Test
    void testValidatePatternWithValidPhoneReturnsNull() {
        BulkLoadError result = ValidationUtils.validatePattern("3125551234", PHONE_PATTERN, "Teléfono inválido", ROW_NUMBER);
        assertNull(result, "Valid phone should return null");
    }

    @Test
    void testValidatePatternWithInvalidPhoneReturnsError() {
        BulkLoadError result = ValidationUtils.validatePattern("123", PHONE_PATTERN, "Teléfono inválido", ROW_NUMBER);
        
        assertNotNull(result, "Invalid phone should return error");
        assertEquals("Teléfono inválido", result.getErrorMessage(), "Error message should match expected");
    }

    @Test
    void testValidateDateWithValidDateReturnsNull() {
        BulkLoadError result = ValidationUtils.validateDate("2024-01-15", DATE_FORMATTER, "Fecha inválida", ROW_NUMBER);
        assertNull(result, "Valid date should return null");
    }

    @Test
    void testValidateDateWithInvalidDateFormatReturnsError() {
        BulkLoadError result = ValidationUtils.validateDate("15/01/2024", DATE_FORMATTER, "Fecha inválida", ROW_NUMBER);
        
        assertNotNull(result, "Invalid date format should return error");
        assertEquals("Fecha inválida", result.getErrorMessage(), "Error message should match expected");
    }

    @Test
    void testValidateDateWithInvalidDateReturnsError() {
        BulkLoadError result = ValidationUtils.validateDate("2024-13-45", DATE_FORMATTER, "Fecha inválida", ROW_NUMBER);
        
        assertNotNull(result, "Invalid date should return error");
        assertEquals("Fecha inválida", result.getErrorMessage(), "Error message should match expected");
    }

    @Test
    void testValidateNumericWithValidNumberReturnsNull() {
        BulkLoadError result = ValidationUtils.validateNumeric("50000.00", "Valor numérico", ROW_NUMBER);
        assertNull(result, "Valid number should return null");
    }

    @Test
    void testValidateNumericWithValidIntegerReturnsNull() {
        BulkLoadError result = ValidationUtils.validateNumeric("50000", "Valor numérico", ROW_NUMBER);
        assertNull(result, "Valid integer should return null");
    }

    @Test
    void testValidateNumericWithInvalidNumberReturnsError() {
        BulkLoadError result = ValidationUtils.validateNumeric("cincuenta mil", "Valor numérico", ROW_NUMBER);
        
        assertNotNull(result, "Invalid number should return error");
        assertTrue(result.getErrorMessage().contains("numérico"), "Error message should contain 'numérico'");
    }

    @Test
    void testValidateNumericWithNegativeNumberReturnsNull() {
        BulkLoadError result = ValidationUtils.validateNumeric("-1000.50", "Valor numérico", ROW_NUMBER);
        assertNull(result, "Negative number should be valid");
    }

    @Test
    void testValidateNumericWithScientificNotationReturnsNull() {
        BulkLoadError result = ValidationUtils.validateNumeric("1.5E+4", "Valor numérico", ROW_NUMBER);
        assertNull(result, "Scientific notation should be valid");
    }
}