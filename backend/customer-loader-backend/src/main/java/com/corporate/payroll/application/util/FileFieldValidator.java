package com.corporate.payroll.application.util;

import com.corporate.payroll.domain.model.BulkLoadError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    public static BulkLoadError validateHeaders(String headerLine, String fileName, LocalDateTime processingDate) {

        if (headerLine == null || headerLine.trim().isEmpty()) {
            return buildErrorHeaders("El archivo está vacío", fileName, processingDate);
        }

        String[] headers = parseCsvLine(headerLine);

        if (headers.length < FileProcessingConstants.MIN_COLUMNS_REQUIRED) {
            return buildErrorHeaders(
                    "El archivo no contiene todas las columnas requeridas. Esperadas: "
                            + FileProcessingConstants.MIN_COLUMNS_REQUIRED + ", Encontradas: " + headers.length,
                    fileName,
                    processingDate
            );
        }

        for (int i = 0; i < FileProcessingConstants.REQUIRED_HEADERS.length; i++) {
            String expectedHeader = FileProcessingConstants.REQUIRED_HEADERS[i];
            String actualHeader = i < headers.length ? headers[i].trim() : "";

            if (!actualHeader.equalsIgnoreCase(expectedHeader)) {
                return buildErrorHeaders(
                        "Header inválido en columna " + (i + 1) + ". Esperado: \""
                                + expectedHeader + "\", Encontrado: \"" + actualHeader + "\"",
                        fileName,
                        processingDate
                );
            }
        }

        return null;
    }

    public static BulkLoadError buildErrorHeaders(
                                            String message, String fileName, LocalDateTime processingDate) {
        return BulkLoadError.builder()
                .lineNumber(FileProcessingConstants.HEADER_ROW)
                .errorMessage(message)
                .errorType(FileProcessingConstants.ErrorType.INVALID_HEADERS.getValue())
                .fileName(fileName)
                .processingDate(processingDate)
                .build();
    }


    public static String identifyMissingFields(String[] values) {
        List<FieldCheck> fields =
                List.of(
                        new FieldCheck(FileProcessingConstants.INDEX_ID_TYPE, FileProcessingConstants.HEADER_ID_TYPE),
                        new FieldCheck(FileProcessingConstants.INDEX_ID_NUMBER, FileProcessingConstants.HEADER_ID_NUMBER),
                        new FieldCheck(FileProcessingConstants.INDEX_JOIN_DATE, FileProcessingConstants.HEADER_JOIN_DATE),
                        new FieldCheck(FileProcessingConstants.INDEX_PAYROLL_VALUE,
                                FileProcessingConstants.HEADER_PAYROLL_VALUE),
                        new FieldCheck(FileProcessingConstants.INDEX_EMAIL, FileProcessingConstants.HEADER_EMAIL),
                        new FieldCheck(FileProcessingConstants.INDEX_PHONE, FileProcessingConstants.HEADER_PHONE)
                );

        List<String> missingFields = fields.stream()
                .filter(f -> values.length <= f.index() ||
                        values[f.index()].trim().isEmpty())
                .map(FieldCheck::header).toList();
        return String.join(", ", missingFields);
    }

    public record FieldCheck(int index, String header) {
    }
}
