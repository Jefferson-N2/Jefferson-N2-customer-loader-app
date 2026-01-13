package com.corporate.payroll.application.service;

import com.corporate.payroll.application.service.dto.RowProcessingContext;
import com.corporate.payroll.application.util.FileProcessingConstants;
import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.service.ClientValidator;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.corporate.payroll.application.util.FileFieldValidator.parseCsvLine;

@Slf4j
@ApplicationScoped
public class RowValidationService {

    public List<BulkLoadError> validateRow(String line, int lineNumber, String processId, 
                                          String fileName,
                                          List<RowProcessingContext> existingRows) {
        
        List<BulkLoadError> errors = new ArrayList<>();
        

        String[] values = parseCsvLine(line);

        if (values.length < FileProcessingConstants.MIN_COLUMNS_REQUIRED) {
            errors.add(createIncompleteRowError(processId, lineNumber, fileName, values.length));
        }

        String idType = getValue(values, FileProcessingConstants.INDEX_ID_TYPE);
        String idNumber = getValue(values, FileProcessingConstants.INDEX_ID_NUMBER);
        String joinDate = getValue(values, FileProcessingConstants.INDEX_JOIN_DATE);
        String payrollValue = getValue(values, FileProcessingConstants.INDEX_PAYROLL_VALUE);
        String email = getValue(values, FileProcessingConstants.INDEX_EMAIL);
        String phoneNumber = getValue(values, FileProcessingConstants.INDEX_PHONE);

        List<BulkLoadError> validationErrors = ClientValidator.validateClient(
                idType, idNumber, joinDate, payrollValue, email, phoneNumber, lineNumber);

        validationErrors.forEach(error -> {
            error.setProcessId(processId);
            error.setFieldName(fileName);
        });

        errors.addAll(validationErrors);

        if (errors.isEmpty() && isDuplicateInFile(idNumber, existingRows)) {
            errors.add(createDuplicateError(processId, lineNumber, fileName));
        }

        return errors;
    }

    public RowProcessingContext createValidContext(String[] values, int lineNumber, 
                                                  String fileName, LocalDateTime processingDate, String processId) {
        return RowProcessingContext.builder()
                .idType(getValue(values, FileProcessingConstants.INDEX_ID_TYPE))
                .idNumber(getValue(values, FileProcessingConstants.INDEX_ID_NUMBER))
                .joinDate(getValue(values, FileProcessingConstants.INDEX_JOIN_DATE))
                .payrollValue(getValue(values, FileProcessingConstants.INDEX_PAYROLL_VALUE))
                .email(getValue(values, FileProcessingConstants.INDEX_EMAIL))
                .phoneNumber(getValue(values, FileProcessingConstants.INDEX_PHONE))
                .lineNumber(lineNumber)
                .fileName(fileName)
                .processingDate(processingDate)
                .processId(processId)
                .build();
    }

    private String getValue(String[] values, int index) {
        return values.length > index ? values[index].trim() : "";
    }

    private boolean isDuplicateInFile(String idNumber, List<RowProcessingContext> existingRows) {
        return existingRows.stream().anyMatch(ctx -> ctx.getIdNumber().equals(idNumber));
    }

    private BulkLoadError createIncompleteRowError(String processId, int lineNumber, String fileName, int fieldsFound) {
        return BulkLoadError.builder()
                .processId(processId)
                .lineNumber(lineNumber)
                .errorMessage("Fila incompleta. Se requieren 6 campos: Tipo ID, Número ID, Fecha ingreso, Valor nómina, Email, Teléfono. Campos encontrados: " + fieldsFound)
                .fieldName(fileName)
                .build();
    }

    private BulkLoadError createDuplicateError(String processId, int lineNumber, String fileName) {
        return BulkLoadError.builder()
                .processId(processId)
                .lineNumber(lineNumber)
                .errorMessage("Número de identificación duplicado en el archivo")
                .fieldName(fileName)
                .build();
    }
}