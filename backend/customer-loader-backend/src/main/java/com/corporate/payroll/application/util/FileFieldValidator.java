package com.corporate.payroll.application.util;

import com.corporate.payroll.domain.model.BulkLoadError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Utilitario para validación de campos faltantes en archivos de carga masiva.
 */
public final class FileFieldValidator {

    private FileFieldValidator() {
    }

    public static String[] parseCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return new String[0];
        }
        
        String separator = line.contains("|") ? "\\|" : ",";
        
        return Arrays.stream(line.split(separator))
                .map(String::trim)
                .toArray(String[]::new);
    }

}
