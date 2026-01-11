package com.corporate.payroll.security;

import com.corporate.payroll.adapter.in.web.security.SecurityValidationInterceptor;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SecurityValidationTest {

    @Test
    void testSanitizeFileNameWithValidFileNameReturnsSame() {
        String validFileName = "clientes_enero_2024.txt";
        String result = SecurityValidationInterceptor.sanitizeFileName(validFileName);
        assertEquals(validFileName, result, "Valid filename should return unchanged");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "archivo.exe",
        "archivo.bat", 
        "archivo.sh",
        "archivo.php",
        "../../../etc/passwd",
        "archivo con espacios.txt",
        "archivo@especial.txt",
        "archivo#hash.txt"
    })
    void testSanitizeFileNameWithInvalidFileNameThrowsException(String invalidFileName) {
        assertThrows(BusinessLogicException.class, () -> {
            SecurityValidationInterceptor.sanitizeFileName(invalidFileName);
        }, "Invalid filename should throw exception: " + invalidFileName);
    }

    @Test
    void testSanitizeFileNameWithNullFileNameThrowsException() {
        assertThrows(BusinessLogicException.class, () -> {
            SecurityValidationInterceptor.sanitizeFileName(null);
        }, "Null filename should throw exception");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "clientes.txt",
        "datos_2024.TXT",
        "archivo-valido.txt",
        "archivo_con_guiones.txt",
        "archivo123.txt"
    })
    void testSanitizeFileNameWithValidFileNamesPasses(String validFileName) {
        assertDoesNotThrow(() -> {
            String result = SecurityValidationInterceptor.sanitizeFileName(validFileName);
            assertEquals(validFileName, result, "Valid filename should be returned unchanged");
        }, "Valid filename should not throw exception: " + validFileName);
    }
}