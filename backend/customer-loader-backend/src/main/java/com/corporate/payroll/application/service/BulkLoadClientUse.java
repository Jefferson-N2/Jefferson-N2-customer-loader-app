package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import com.corporate.payroll.application.port.in.BulkLoadClientUseCase;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.PayrollPaymentRepositoryPort;
import com.corporate.payroll.application.port.out.DatabookPort;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.application.service.dto.RowProcessingContext;
import com.corporate.payroll.application.util.FileProcessingConstants;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.PayrollPayment;
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
    private final PayrollPaymentRepositoryPort paymentRepository;
    private final DatabookPort databookService;
    private final BulkLoadErrorRepositoryPort errorRepository;
    private final BulkLoadProcessRepositoryPort bulkLoadProcessRepository;
    private final ClientFactory clientFactory;
    private final AccountFactory accountFactory;

    @Inject
    public BulkLoadClientUse(ClientRepositoryPort clientRepository,
                             AccountRepositoryPort accountRepository,
                             PayrollPaymentRepositoryPort paymentRepository,
                             DatabookPort databookService,
                             BulkLoadErrorRepositoryPort errorRepository,
                             BulkLoadProcessRepositoryPort bulkLoadProcessRepository,
                             ClientFactory clientFactory,
                             AccountFactory accountFactory) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
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

        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            BulkLoadProcess bulkLoadProcess = BulkLoadProcess.builder()
                    .processId(processId)
                    .fileName(fileName)
                    .status("PROCESSING")
                    .processingDate(processingDate)
                    .successfulCount(0)
                    .errorCount(0)
                    .build();
            bulkLoadProcessRepository.save(bulkLoadProcess);

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

                RowProcessingContext context = RowProcessingContext.builder()
                        .idType(idType)
                        .idNumber(idNumber)
                        .joinDate(joinDate)
                        .payrollValue(payrollValue)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .lineNumber(lineNumber)
                        .fileName(fileName)
                        .processingDate(processingDate)
                        .processId(processId)
                        .build();

                if (processValidRow(context)) {
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

    private boolean processValidRow(RowProcessingContext context) {
        boolean hasErrors = false;
        List<BulkLoadError> errors = new ArrayList<>();
        
        try {
            // Verificar si el cliente ya existe
            if (clientRepository.existsByIdNumber(context.getIdNumber())) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(context.getProcessId())
                        .idType(context.getIdType())
                        .idNumber(context.getIdNumber())
                        .lineNumber(context.getLineNumber())
                        .errorMessage("El cliente con este número de identificación ya existe")
                        .errorType(FileProcessingConstants.ErrorType.DUPLICATE_CLIENT.getValue())
                        .fileName(context.getFileName())
                        .processingDate(context.getProcessingDate())
                        .build();
                errors.add(error);
                hasErrors = true;
            }

            // Consultar databook
            Optional<DatabookResponseDto> databookData = databookService.getClientInfo(context.getIdType(), context.getIdNumber());
            
            if (databookData.isEmpty()) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(context.getProcessId())
                        .idType(context.getIdType())
                        .idNumber(context.getIdNumber())
                        .lineNumber(context.getLineNumber())
                        .errorMessage("Cliente no encontrado en el servicio externo (Databook)")
                        .errorType(FileProcessingConstants.ErrorType.NOT_FOUND_IN_DATABOOK.getValue())
                        .fileName(context.getFileName())
                        .processingDate(context.getProcessingDate())
                        .build();
                errors.add(error);
                hasErrors = true;
            }

            // Si hay errores críticos, registrarlos y retornar false
            if (hasErrors) {
                errorRepository.saveAll(errors);
                return false;
            }

            // Crear cliente
            String clientCode = clientFactory.generateUniqueClientCode();
            if (clientCode == null) {
                BulkLoadError error = BulkLoadError.builder()
                        .processId(context.getProcessId())
                        .clientCode(context.getIdNumber())
                        .idType(context.getIdType())
                        .idNumber(context.getIdNumber())
                        .lineNumber(context.getLineNumber())
                        .errorMessage("No se pudo generar un código de cliente único después de múltiples intentos")
                        .errorType(FileProcessingConstants.ErrorType.SYSTEM_ERROR.getValue())
                        .fileName(context.getFileName())
                        .processingDate(context.getProcessingDate())
                        .build();
                errorRepository.saveAll(Arrays.asList(error));
                return false;
            }
            
            Client client = Client.builder()
                    .clientCode(clientCode)
                    .idType(context.getIdType())
                    .idNumber(context.getIdNumber())
                    .firstNames(databookData.get().getFirstNames())
                    .lastNames(databookData.get().getLastNames())
                    .birthDate(LocalDate.parse(databookData.get().getBirthDate(), DATE_FORMATTER))
                    .joinDate(LocalDate.parse(context.getJoinDate(), DATE_FORMATTER))
                    .email(context.getEmail())
                    .phoneNumber(context.getPhoneNumber())
                    .processId(context.getProcessId())
                    .build();

            Client savedClient = clientRepository.save(client);

            String accountNumber = accountFactory.generateUniqueAccountNumber();

            Account account = Account.builder()
                    .accountNumber(accountNumber)
                    .clientId(savedClient.getId())
                    .payrollValue(new BigDecimal(context.getPayrollValue()))
                    .status(FileProcessingConstants.DEFAULT_ACCOUNT_STATUS)
                    .build();
            Account savedAccount = accountRepository.save(account);
            
            PayrollPayment initialPayment = PayrollPayment.builder()
                    .accountId(savedAccount.getId())
                    .paymentDate(LocalDate.parse(context.getJoinDate(), DATE_FORMATTER))
                    .amount(new BigDecimal(context.getPayrollValue()))
                    .status("PENDING")
                    .build();
            paymentRepository.save(initialPayment);

            return true;
        } catch (Exception e) {
            String sanitizedMessage = e.getMessage() != null ? 
                e.getMessage().replaceAll("[\r\n\t]", "_") : "Error desconocido";
            log.error("Error procesando cliente en fila {}: {}", context.getLineNumber(), sanitizedMessage);
            
            BulkLoadError error = BulkLoadError.builder()
                    .processId(context.getProcessId())
                    .idType(context.getIdType())
                    .idNumber(context.getIdNumber())
                    .lineNumber(context.getLineNumber())
                    .errorMessage("Error al procesar: " + sanitizedMessage)
                    .errorType("PROCESSING_ERROR")
                    .fileName(context.getFileName())
                    .processingDate(context.getProcessingDate())
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

   
}


