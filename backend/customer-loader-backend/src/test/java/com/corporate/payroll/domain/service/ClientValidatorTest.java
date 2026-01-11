package com.corporate.payroll.domain.service;

import com.corporate.payroll.domain.model.BulkLoadError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para ClientValidator que devuelve List<BulkLoadError>
 * Los validadores no lanzan excepciones, sino que retornan errores en una lista
 */
class ClientValidatorTest {

    private static final Integer ROW_NUMBER = 1;

    @Test
    void testValidateClientWithValidDataShouldReturnEmptyList() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertEquals(0, errors.size(), "No debería haber errores con datos válidos");
    }

    @Test
    void testValidateClientWithInvalidIdTypeShouldReturnError() {
        String idType = "X"; 
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("Tipo de identificación debe ser")),
                "Debe haber error sobre tipo de identificación");
    }

    @Test
    void testValidateClientWithBlankIdTypeShouldReturnError() {
        String idType = "";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("no puede estar vacío")),
                "Debe haber error sobre campo vacío");
    }

    @Test
    void testValidateClientWithInvalidIdNumberShouldReturnError() {
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
                "Debe haber error sobre número alfanumérico");
    }

    @Test
    void testValidateClientWithInvalidDateShouldReturnError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "15/01/2025";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("yyyy-MM-dd")),
                "Debe haber error sobre formato de fecha");
    }

    @Test
    void testValidateClientWithInvalidPayrollValueShouldReturnError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "cincuenta mil"; // No es numérico
        String email = "juan.perez@empresa.com";
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("numérico") ||
                                e.getErrorMessage().contains("Valor del pago")),
                "Debe haber error sobre valor numérico");
    }

    @Test
    void testValidateClientWithInvalidEmailShouldReturnError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "invalid-email"; // Sin @
        String phoneNumber = "3125551234";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("Correo")),
                "Debe haber error sobre formato de correo");
    }

    @Test
    void testValidateClientWithInvalidPhoneNumberShouldReturnError() {
        String idType = "C";
        String idNumber = "12345678";
        String joinDate = "2025-01-15";
        String payrollValue = "50000.00";
        String email = "juan.perez@empresa.com";
        String phoneNumber = "312555"; // Solo 6 dígitos

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("10 dígitos")),
                "Debe haber error sobre 10 dígitos");
    }

    @Test
    void testValidateClientWithMultipleErrorsShouldReturnAllErrors() {
        String idType = "X";
        String idNumber = "ABC@#$";
        String joinDate = "invalid";
        String payrollValue = "texto";
        String email = "no-email";
        String phoneNumber = "123";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.size() > 5, "Debe haber más de 5 errores");

        assertTrue(errors.stream().allMatch(e -> e.getRowNumber().equals(ROW_NUMBER)),
                "Todos los errores deben tener el rowNumber correcto");

        assertTrue(errors.stream().allMatch(e -> "VALIDATION_ERROR".equals(e.getErrorType())),
                "Todos los errores deben ser de tipo VALIDATION_ERROR");
    }

    @Test
    void testValidateClientWithNullFieldsShouldReturnErrors() {
        String idType = null;
        String idNumber = null;
        String joinDate = null;
        String payrollValue = null;
        String email = null;
        String phoneNumber = null;

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertTrue(errors.stream().anyMatch(e ->
                        e.getErrorMessage().contains("no puede estar vacío")),
                "Debe haber errores sobre campos vacíos");
    }

    @Test
    void testValidateClientWithValidPassportShouldPass() {
        String idType = "P";
        String idNumber = "AB123456";
        String joinDate = "2025-02-20";
        String payrollValue = "75000.50";
        String email = "maria.garcia@empresa.com";
        String phoneNumber = "3185559876";

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, ROW_NUMBER);

        assertEquals(0, errors.size(), "No debería haber errores con pasaporte válido");
    }

    @Test
    void testValidateClientErrorsIncludeCorrectRowNumber() {
        String idType = "X";
        Integer rowNumber = 42;

        List<BulkLoadError> errors = ClientValidator.validateClient(
                idType, "12345678", "2025-01-15", "50000", "test@test.com", "3125551234", rowNumber);

        assertTrue(errors.stream().allMatch(e -> e.getRowNumber().equals(rowNumber)),
                "Todos los errores deben tener rowNumber = 42");
    }
}

