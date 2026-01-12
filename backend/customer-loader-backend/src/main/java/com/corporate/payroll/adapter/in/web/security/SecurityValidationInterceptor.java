package com.corporate.payroll.adapter.in.web.security;

import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.regex.Pattern;

@Interceptor
@SecurityValidation
@Slf4j
public class SecurityValidationInterceptor {

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; 
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+\\.(txt|TXT)$");
    private static final Pattern XSS_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)");

    @AroundInvoke
    public Object validateSecurity(InvocationContext context) throws Exception {
        Object[] parameters = context.getParameters();
        
        for (Object param : parameters) {
            if (param instanceof String) {
                validateStringInput((String) param);
            }
        }
        
        return context.proceed();
    }

    private void validateStringInput(String input) {
        if (input == null) return;
        
        if (XSS_PATTERN.matcher(input).find()) {
            log.warn("XSS detectado en entrada: {}", input.substring(0, Math.min(50, input.length())));
            throw new BusinessLogicException("Entrada contiene caracteres no permitidos");
        }
        
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            log.warn("SQL injection detectado en entrada: {}", input.substring(0, Math.min(50, input.length())));
            throw new BusinessLogicException("Entrada contiene patrones de SQL injection");
        }
    }

    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessLogicException("Nombre de archivo requerido");
        }
        
        if (!SAFE_FILENAME_PATTERN.matcher(fileName).matches()) {
            throw new BusinessLogicException("Nombre de archivo no válido. Solo se permiten letras, números, puntos, guiones y extensión .txt");
        }
        
        return fileName.trim();
    }
}