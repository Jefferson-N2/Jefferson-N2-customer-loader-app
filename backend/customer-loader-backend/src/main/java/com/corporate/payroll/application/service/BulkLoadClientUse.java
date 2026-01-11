package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import com.corporate.payroll.application.port.in.BulkLoadClientUseCase;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.DatabookPort;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.application.util.FileProcessingConstants;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import com.corporate.payroll.domain.service.ClientValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.*;

import static com.corporate.payroll.application.util.FileFieldValidator.*;

@Slf4j
@ApplicationScoped
@Transactional
public class BulkLoadClientUse implements BulkLoadClientUseCase {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ClientRepositoryPort clientRepository;
    private final AccountRepositoryPort accountRepository;
    private final DatabookPort databookService;
    private final BulkLoadErrorRepositoryPort errorRepository;
    private final BulkLoadProcessRepositoryPort bulkLoadProcessRepository;
    private final ClientFactory clientFactory;
    private final AccountFactory accountFactory;

    @Inject
    public BulkLoadClientUse(ClientRepositoryPort clientRepository,
                             AccountRepositoryPort accountRepository,
                             DatabookPort databookService,
                             BulkLoadErrorRepositoryPort errorRepository,
                             BulkLoadProcessRepositoryPort bulkLoadProcessRepository,
                             ClientFactory clientFactory,
                             AccountFactory accountFactory) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.databookService = databookService;
        this.errorRepository = errorRepository;
        this.bulkLoadProcessRepository = bulkLoadProcessRepository;
        this.clientFactory = clientFactory;
        this.accountFactory = accountFactory;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BulkLoadStatisticsResponseDto processBulkLoad(InputStream fileStream, String fileName) {
        if (fileStream == null) {
            throw new BusinessLogicException("El archivo es requerido");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessLogicException("El archivo no tiene nombre");
        }

        String processId = UUID.randomUUID().toString();
        LocalDateTime processingDate = LocalDateTime.now();
        fileName = appendTimestampToFileName(fileName, processingDate);

        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            // Crear registro del proceso
            BulkLoadProcess bulkLoadProcess = BulkLoadProcess.builder()
                    .processId(processId)
                    .fileName(fileName)
                    .status("PROCESSING")
                    .processingDate(processingDate)
                    .successfulCount(0)
                    .errorCount(0)
                    .build();
            bulkLoadProcessRepository.save(bulkLoadProcess);

            // Validar encabezados del archivo
            String headerLine = reader.readLine();
            BulkLoadError headerError = validateHeaders(headerLine, fileName, processingDate, processId);
            if (headerError != null) {
                errorRepository.saveAll(Arrays.asList(headerError));
                bulkLoadProcess.setStatus("FAILED");
                bulkLoadProcess.setErrorCount(1);
                bulkLoadProcessRepository.update(bulkLoadProcess);
                throw new BusinessLogicException(headerError.getErrorMessage());
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    lineNumber++;
                    continue;
                }

                String[] values = parseCsvLine(line);

                if (values.length < FileProcessingConstants.MIN_COLUMNS_REQUIRED) {
                    String missingFields = identifyMissingFields(values);
                    BulkLoadError error = BulkLoadError.builder()
                            .processId(processId)
                            .lineNumber(lineNumber)
                            .errorMessage("Fila incompleta. Campos faltantes: " + missingFields)
                            .errorType(FileProcessingConstants.ErrorType.MISSING_FIELD.getValue())
                            .fileName(fileName)
                            .processingDate(processingDate)
                            .build();
                    errorRepository.saveAll(Arrays.asList(error));
                    errorCount++;
                    lineNumber++;
                    continue;
                }

                String idType = values[FileProcessingConstants.INDEX_ID_TYPE].trim();
                String idNumber = values[FileProcessingConstants.INDEX_ID_NUMBER].trim();
                String joinDate = values[FileProcessingConstants.INDEX_JOIN_DATE].trim();
                String payrollValue = values[FileProcessingConstants.INDEX_PAYROLL_VALUE].trim();
                String email = values[FileProcessingConstants.INDEX_EMAIL].trim();
                String phoneNumber = values[FileProcessingConstants.INDEX_PHONE].trim();

                List<BulkLoadError> validationErrors = ClientValidator.validateClient(
                        idType, idNumber, joinDate, payrollValue, email, phoneNumber, lineNumber
                );

                String finalFileName = fileName;
                validationErrors = validationErrors.stream()
                        .filter(Objects::nonNull)
                        .peek(error -> {
                            error.setProcessId(processId);
                            error.setFileName(finalFileName);
                            error.setProcessingDate(processingDate);
                            error.setIdType(idType);
                            error.setIdNumber(idNumber);
                        })
                        .toList();

                if (!validationErrors.isEmpty()) {
                    errorRepository.saveAll(validationErrors);
                    errorCount++;
                    lineNumber++;
                    continue;
                }

                if (processValidRow(idType, idNumber, joinDate, payrollValue, email, phoneNumber,
                        lineNumber, fileName, processingDate, processId, clientFactory, accountFactory)) {
                    successCount++;
                } else {
                    errorCount++;
                }

                lineNumber++;
            }

            // Actualizar registro del proceso con resultados finales
            bulkLoadProcess.setStatus("COMPLETED");
            bulkLoadProcess.setSuccessfulCount(successCount);
            bulkLoadProcess.setErrorCount(errorCount);
            bulkLoadProcess.setTotalRecords(successCount + errorCount);
            bulkLoadProcessRepository.update(bulkLoadProcess);

            return buildStatisticsResponse(successCount, errorCount, processId);

        } catch (IOException e) {
            String sanitizedMessage = e.getMessage() != null ? 
                e.getMessage().replaceAll("[\r\n\t]", "_") : "Error desconocido";
            log.error("Error al leer el archivo: {}", sanitizedMessage, e);
            throw new BusinessLogicException("Error al leer el archivo: " + sanitizedMessage);
        } catch (Exception e) {
            String sanitizedMessage = e.getMessage() != null ? 
                e.getMessage().replaceAll("[\r\n\t]", "_") : "Error desconocido";
            log.error("Error inesperado procesando archivo: {}", sanitizedMessage, e);
            throw new BusinessLogicException("Error procesando archivo: " + sanitizedMessage);
        }
    }

    /**
     * Procesa una fila válida: verifica duplicados, consulta databook, crea cliente y cuenta
     */
    private boolean processValidRow(String idType, String idNumber, String joinDate,
                                   String payrollValue, String email, String phoneNumber,
                                   Integer lineNumber, String fileName, LocalDateTime processingDate,
                                   String processId, ClientFactory clientFactory, AccountFactory accountFactory) {
        try {
            if (clientRepository.existsByIdNumber(idNumber)) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(processId)
                        .idType(idType)
                        .idNumber(idNumber)
                        .lineNumber(lineNumber)
                        .errorMessage("El cliente con este número de identificación ya existe")
                        .errorType(FileProcessingConstants.ErrorType.DUPLICATE_CLIENT.getValue())
                        .fileName(fileName)
                        .processingDate(processingDate)
                        .build();
                errorRepository.saveAll(Arrays.asList(error));
                return false;
            }

            Optional<DatabookResponseDto> databookData = databookService.getClientInfo(idType, idNumber);

            if (databookData.isEmpty()) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(processId)
                        .idType(idType)
                        .idNumber(idNumber)
                        .lineNumber(lineNumber)
                        .errorMessage("Cliente no encontrado en el servicio externo (Databook)")
                        .errorType(FileProcessingConstants.ErrorType.NOT_FOUND_IN_DATABOOK.getValue())
                        .fileName(fileName)
                        .processingDate(processingDate)
                        .build();
                errorRepository.saveAll(Arrays.asList(error));
                return false;
            }

            Client client = Client.builder()
                    .clientCode(clientFactory.generateUniqueClientCode())
                    .idType(idType)
                    .idNumber(idNumber)
                    .firstNames(databookData.get().getFirstNames())
                    .lastNames(databookData.get().getLastNames())
                    .birthDate(LocalDate.parse(databookData.get().getBirthDate(), DATE_FORMATTER))
                    .joinDate(LocalDate.parse(joinDate, DATE_FORMATTER))
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .processId(processId)
                    .build();

            // Validar que el clientCode fue generado correctamente
            if (client.getClientCode() == null) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(processId)
                        .clientCode(idNumber)
                        .idType(idType)
                        .idNumber(idNumber)
                        .lineNumber(lineNumber)
                        .errorMessage("No se pudo generar un código de cliente único después de múltiples intentos")
                        .errorType(FileProcessingConstants.ErrorType.SYSTEM_ERROR.getValue())
                        .fileName(fileName)
                        .processingDate(processingDate)
                        .build();
                errorRepository.saveAll(Arrays.asList(error));
                return false;
            }

            Client savedClient = clientRepository.save(client);

            String accountNumber = accountFactory.generateUniqueAccountNumber();
            
            if (accountNumber == null) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(processId)
                        .clientCode(client.getClientCode())
                        .idType(idType)
                        .idNumber(idNumber)
                        .lineNumber(lineNumber)
                        .errorMessage("No se pudo generar un número de cuenta único después de múltiples intentos")
                        .errorType(FileProcessingConstants.ErrorType.SYSTEM_ERROR.getValue())
                        .fileName(fileName)
                        .processingDate(processingDate)
                        .build();
                errorRepository.saveAll(Arrays.asList(error));
                return false;
            }

            Account account = Account.builder()
                    .accountNumber(accountNumber)
                    .clientId(savedClient.getId())
                    .payrollValue(new BigDecimal(payrollValue))
                    .status(FileProcessingConstants.DEFAULT_ACCOUNT_STATUS)
                    .build();
            accountRepository.save(account);

            return true;
        } catch (Exception e) {
            String sanitizedMessage = e.getMessage() != null ? 
                e.getMessage().replaceAll("[\r\n\t]", "_") : "Error desconocido";
            log.error("Error procesando cliente en fila {}: {}", lineNumber, sanitizedMessage);
            
            BulkLoadError error = BulkLoadError.builder()
                    .processId(processId)
                    .idType(idType)
                    .idNumber(idNumber)
                    .lineNumber(lineNumber)
                    .errorMessage("Error al procesar: " + sanitizedMessage)
                    .errorType("PROCESSING_ERROR")
                    .fileName(fileName)
                    .processingDate(processingDate)
                    .build();
            errorRepository.saveAll(Arrays.asList(error));
            return false;
        }
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

    private BulkLoadError validateHeaders(String headerLine, String fileName, LocalDateTime processingDate, String processId) {
        if (headerLine == null || headerLine.trim().isEmpty()) {
            return BulkLoadError.builder()
                    .processId(processId)
                    .lineNumber(0)
                    .errorMessage("El archivo no contiene encabezados válidos")
                    .errorType(FileProcessingConstants.ErrorType.INVALID_FORMAT.getValue())
                    .fileName(fileName)
                    .processingDate(processingDate)
                    .build();
        }
        return null;
    }

    private String appendTimestampToFileName(String originalFileName, LocalDateTime dateTime) {
        String sanitizedFileName = originalFileName != null ? 
            originalFileName.replaceAll("[\r\n\t]", "_") : "unknown";
        String timestamp = dateTime.format(DateTimeFormatter.ofPattern("ddMMyyyyHH:mm:ss"));

        if (sanitizedFileName.contains(".")) {
            int lastDotIndex = sanitizedFileName.lastIndexOf(".");
            String nameWithoutExtension = sanitizedFileName.substring(0, lastDotIndex);
            String extension = sanitizedFileName.substring(lastDotIndex);
            return nameWithoutExtension + "_" + timestamp + extension;
        } else {
            return sanitizedFileName + "_" + timestamp + ".txt";
        }
    }
}

