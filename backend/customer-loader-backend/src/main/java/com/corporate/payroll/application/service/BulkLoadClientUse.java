package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import com.corporate.payroll.application.port.in.BulkLoadClientUseCase;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.application.service.dto.RowProcessingContext;
import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

import static com.corporate.payroll.application.util.FileFieldValidator.parseCsvLine;

@Slf4j
@ApplicationScoped
@Transactional
public class BulkLoadClientUse implements BulkLoadClientUseCase {

    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;

    @Inject
    private BulkLoadProcessRepositoryPort bulkLoadProcessRepository;

    @Inject
    private RowValidationService rowValidationService;

    @Inject
    private ClientProcessingService clientProcessingService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BulkLoadStatisticsResponseDto processBulkLoad(InputStream fileStream, String fileName) {
        validateInput(fileStream, fileName);

        String processId = UUID.randomUUID().toString();
        LocalDateTime processingDate = LocalDateTime.now();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            BulkLoadProcess process = createBulkLoadProcess(processId, fileName, processingDate);
            bulkLoadProcessRepository.save(process);

            ProcessingResult result = processFile(reader, processId, fileName, processingDate);

            updateProcessStatus(process, result);
            bulkLoadProcessRepository.update(process);

            return buildStatisticsResponse(result.successCount, result.errorCount, processId);

        } catch (IOException e) {
            log.error("Error al leer el archivo: {}", e.getMessage(), e);
            throw new BusinessLogicException("Error al leer el archivo");
        } catch (Exception e) {
            log.error("Error inesperado procesando archivo: {}", e.getMessage(), e);
            throw new BusinessLogicException("Error procesando archivo");
        }
    }

    private void validateInput(InputStream fileStream, String fileName) {
        if (fileStream == null) {
            throw new BusinessLogicException("El archivo es requerido");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessLogicException("El archivo no tiene nombre");
        }
    }

    private ProcessingResult processFile(BufferedReader reader, String processId,
                                         String fileName, LocalDateTime processingDate) throws IOException {
        List<RowProcessingContext> validRows = new ArrayList<>();
        List<BulkLoadError> allErrors = new ArrayList<>();

        String line;
        int lineNumber = 1;
        int totalLinesRead = 0;

        log.info("Iniciando procesamiento de archivo: {}", fileName);

        while ((line = reader.readLine()) != null) {
            totalLinesRead++;
            log.debug("Procesando línea {}: {}", lineNumber, line);

            if (line.trim().isEmpty()) {
                log.debug("Línea {} vacía, saltando", lineNumber);
                lineNumber++;
                continue;
            }

            // Validar la línea
            List<BulkLoadError> rowErrors = rowValidationService.validateRow(
                    line, lineNumber, processId, fileName, validRows);

            if (rowErrors.isEmpty()) {
                String[] values = parseCsvLine(line);
                RowProcessingContext context = rowValidationService.createValidContext(
                        values, lineNumber, fileName, processingDate, processId);
                validRows.add(context);
                log.debug("Línea {} válida agregada", lineNumber);
            } else {
                allErrors.addAll(rowErrors);
                log.debug("Línea {} con {} errores", lineNumber, rowErrors.size());
            }
            lineNumber++;
        }

        log.info("Archivo procesado: {} líneas leídas, {} válidas, {} con errores",
                totalLinesRead, validRows.size(), allErrors.size());

        // Persistir TODOS los errores de validación
        if (!allErrors.isEmpty()) {
            log.info("Guardando {} errores de validación", allErrors.size());
            errorRepository.saveAll(allErrors);
        }

        // Procesar las filas válidas
        ProcessingResult result = processValidRows(validRows);
        
        // Sumar los errores de validación al resultado
        result = new ProcessingResult(result.successCount, result.errorCount + allErrors.size());
        
        return result;
    }

    private ProcessingResult processValidRows(List<RowProcessingContext> validRows) {
        int successCount = 0;
        int errorCount = 0;

        log.info("Procesando {} registros válidos", validRows.size());
        for (RowProcessingContext context : validRows) {
            if (clientProcessingService.processClient(context)) {
                successCount++;
            } else {
                errorCount++;
            }
        }

        return new ProcessingResult(successCount, errorCount);
    }

    private BulkLoadProcess createBulkLoadProcess(String processId, String fileName, LocalDateTime processingDate) {
        return BulkLoadProcess.builder()
                .processId(processId)
                .fileName(fileName)
                .status("PROCESSING")
                .processingDate(processingDate)
                .successfulCount(0)
                .errorCount(0)
                .build();
    }

    private void updateProcessStatus(BulkLoadProcess process, ProcessingResult result) {
        process.setStatus(result.errorCount > 0 && result.successCount == 0 ? "ERROR" : "COMPLETED");
        process.setSuccessfulCount(result.successCount);
        process.setErrorCount(result.errorCount);
        process.setTotalRecords(result.successCount + result.errorCount);
    }

    private BulkLoadStatisticsResponseDto buildStatisticsResponse(int successCount, int errorCount, String processId) {
        int totalCount = successCount + errorCount;
        String message = String.format("Procesamiento completado. Exitosos: %d, Errores: %d",
                successCount, errorCount);

        return BulkLoadStatisticsResponseDto.builder()
                .processId(processId)
                .successfulCount(successCount)
                .errorCount(errorCount)
                .totalCount(totalCount)
                .message(message)
                .processedAt(LocalDateTime.now())
                .build();
    }

    private String sanitizeMessage(String message) {
        return message != null ? message.replaceAll("[\r\n\t]", "_") : "Error desconocido";
    }

    private static class ProcessingResult {
        final int successCount;
        final int errorCount;

        ProcessingResult(int successCount, int errorCount) {
            this.successCount = successCount;
            this.errorCount = errorCount;
        }
    }
}