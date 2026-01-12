package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import com.corporate.payroll.application.port.out.*;
import com.corporate.payroll.application.service.dto.RowProcessingContext;
import com.corporate.payroll.application.util.FileProcessingConstants;
import com.corporate.payroll.domain.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClientProcessingService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private ClientRepositoryPort clientRepository;
    
    @Inject
    private AccountRepositoryPort accountRepository;
    
    @Inject
    private PayrollPaymentRepositoryPort paymentRepository;
    
    @Inject
    private DatabookPort databookService;
    
    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;
    
    @Inject
    private ClientFactory clientFactory;
    
    @Inject
    private AccountFactory accountFactory;

    public boolean processClient(RowProcessingContext context) {
        try {
            if (clientRepository.existsByIdNumber(context.getIdNumber())) {
                saveError(context, "El cliente con este número de identificación ya existe", 
                         FileProcessingConstants.ErrorType.DUPLICATE_CLIENT.getValue());
                return false;
            }

            Optional<DatabookResponseDto> databookData = databookService.getClientInfo(
                    context.getIdType(), context.getIdNumber());
            
            if (databookData.isEmpty()) {
                saveError(context, "Cliente no encontrado en el servicio externo (Databook)", 
                         FileProcessingConstants.ErrorType.NOT_FOUND_IN_DATABOOK.getValue());
                return false;
            }

            String clientCode = clientFactory.generateUniqueClientCode();
            if (clientCode == null) {
                saveError(context, "No se pudo generar un código de cliente único", 
                         FileProcessingConstants.ErrorType.SYSTEM_ERROR.getValue());
                return false;
            }

            Client client = createClient(context, databookData.get(), clientCode);
            Client savedClient = clientRepository.save(client);

            Account account = createAccount(context, savedClient);
            Account savedAccount = accountRepository.save(account);

            PayrollPayment payment = createInitialPayment(context, savedAccount);
            paymentRepository.save(payment);

            return true;

        } catch (Exception e) {
            log.error("Error procesando cliente en fila {}: {}", context.getLineNumber(), e.getMessage());
            saveError(context, "Error al procesar: " + sanitizeMessage(e.getMessage()), "PROCESSING_ERROR");
            return false;
        }
    }

    private Client createClient(RowProcessingContext context, DatabookResponseDto databookData, String clientCode) {
        return Client.builder()
                .clientCode(clientCode)
                .idType(context.getIdType())
                .idNumber(context.getIdNumber())
                .firstNames(databookData.getFirstNames())
                .lastNames(databookData.getLastNames())
                .birthDate(LocalDate.parse(databookData.getBirthDate(), DATE_FORMATTER))
                .joinDate(LocalDate.parse(context.getJoinDate(), DATE_FORMATTER))
                .email(context.getEmail())
                .phoneNumber(context.getPhoneNumber())
                .processId(context.getProcessId())
                .build();
    }

    private Account createAccount(RowProcessingContext context, Client client) {
        String accountNumber = accountFactory.generateUniqueAccountNumber();
        return Account.builder()
                .accountNumber(accountNumber)
                .clientId(client.getId())
                .payrollValue(new BigDecimal(context.getPayrollValue()))
                .status(FileProcessingConstants.DEFAULT_ACCOUNT_STATUS)
                .build();
    }

    private PayrollPayment createInitialPayment(RowProcessingContext context, Account account) {
        return PayrollPayment.builder()
                .accountId(account.getId())
                .paymentDate(LocalDate.parse(context.getJoinDate(), DATE_FORMATTER))
                .amount(new BigDecimal(context.getPayrollValue()))
                .status("PENDING")
                .build();
    }

    private void saveError(RowProcessingContext context, String message, String errorType) {
        BulkLoadError error = BulkLoadError.builder()
                .processId(context.getProcessId())
                .lineNumber(context.getLineNumber())
                .errorMessage(message)
                .fieldName(context.getFileName())
                .build();
        errorRepository.saveAll(List.of(error));
    }

    private String sanitizeMessage(String message) {
        return message != null ? message.replaceAll("[\\r\\n\\t]", "_") : "Error desconocido";
    }
}