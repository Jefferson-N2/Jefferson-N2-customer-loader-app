package com.corporate.payroll.domain.service;

import com.corporate.payroll.domain.model.BulkLoadError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientValidatorTest {

    private static final Integer ROW_NUMBER = 1;

    @Test
    void testValidateClientWithValidDataReturnsEmptyList() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertEquals(0, errors.size(), "Valid data should return empty error list");
        assertTrue(errors.isEmpty(), "Error list should be empty for valid data");
    }

    @Test
    void testValidateClientWithInvalidIdTypeReturnsError() {
        String idType = "X";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

    }

    @Test
    void testValidateClientWithBlankIdTypeReturnsError() {
        String idType = "";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

    }

    @Test
    void testValidateClientWithInvalidIdNumberReturnsError() {
        String idType = "C";
        String idNumber = "123-456!";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("alfanumérico")),
                "Should return error for non-alphanumeric ID number");
    }

    @Test
    void testValidateClientWithInvalidDateReturnsError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "15/01/2025";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertFalse(errors.isEmpty(), "Should return errors for invalid date format");
        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("yyyy-MM-dd")),
                "Should return error for incorrect date format");
    }

    @Test
    void testValidateClientWithInvalidPayrollValueReturnsError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "cincuenta mil"; 
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("numérico") ||
                                e.getErrorMessage().contains("Valor del pago")),
                "Should return error for non-numeric payroll value");
    }

    @Test
    void testValidateClientWithInvalidEmailReturnsError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "invalid-email"; 
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("Correo")),
                "Should return error for invalid email format");
    }

    @Test
    void testValidateClientWithInvalidPhoneNumberReturnsError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "312555";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("10 dígitos")),
                "Should return error for phone number not having 10 digits");
    }

    @Test
    void testValidateClientWithMultipleErrorsReturnsAllErrors() {
        String idType = "X";
        String idNumber = "ABC@#$";
        String joinDate = "invalid";
        String payrollValue = "texto";
        String email = "no-email";
        String phoneNumber = "123";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.size() > 5, "Should return multiple errors for multiple invalid fields");
        assertTrue(errors.stream().allMatch(e -> e.getLineNumber().equals(ROW_NUMBER)),
                "All errors should have correct line number");
    }

    @Test
    void testValidateClientWithValidPassportPasses() {
        String idType = "P";
        String idNumber = "AB123456";
        String joinDate = "2025-02-20";
        String payrollValue = "75000.50";
        String email = "maria.garcia@empresa.com";
        String phoneNumber = "3185559876";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertEquals(0, errors.size(), "Valid passport data should return no errors");
        assertTrue(errors.isEmpty(), "Error list should be empty for valid passport");
    }

    @Test
    void testValidateClientErrorsIncludeCorrectLineNumber() {
        String idType = "X";
        Integer lineNumber = 42;

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, "12345678", "2025-01-15", "50000", "test@test.com", "3125551234", lineNumber);

        assertTrue(errors.stream().allMatch(e -> e.getLineNumber().equals(lineNumber)),
                "All errors should have the specified line number");
    }
}