package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.*;
import com.corporate.payroll.application.port.in.BulkLoadQueryPort;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.BulkLoadError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para consultar datos de clientes y errores de procesos de carga masiva
 */
@ApplicationScoped
public class BulkLoadQueryUse implements BulkLoadQueryPort {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final ClientRepositoryPort clientRepository;
    private final AccountRepositoryPort accountRepository;
    private final BulkLoadErrorRepositoryPort errorRepository;
    
    @Inject
    public BulkLoadQueryUse(ClientRepositoryPort clientRepository,
                            AccountRepositoryPort accountRepository,
                            BulkLoadErrorRepositoryPort errorRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.errorRepository = errorRepository;
    }
    
    /**
     * Obtiene los clientes creados en un proceso de carga (paginado)
     */
    @Override
    public ClientsPageResponseDto getClientsByProcess(String processId, int page, int size) {
        List<Client> clients = clientRepository.findByProcessId(processId, page, size);
        long totalClients = clientRepository.countByProcessId(processId);
        int totalPages = (int) Math.ceil((double) totalClients / size);
        
        List<ClientDetailDto> clientDetails = clients.stream()
                .map(this::mapToClientDetailDto)
                .collect(Collectors.toList());
        
        return ClientsPageResponseDto.builder()
                .processId(processId)
                .page(page)
                .pageSize(size)
                .totalPages(totalPages)
                .clients(clientDetails)
                .build();
    }
    
    /**
     * Obtiene los errores asociados a un cliente específico en un proceso
     */
    @Override
    public ClientErrorsResponseDto getClientErrors(String processId, String clientCode) {
        List<BulkLoadError> errors = errorRepository.findByClientCodeAndProcessId(processId, clientCode);
        
        List<ClientErrorDto> errorDtos = errors.stream()
                .map(error -> ClientErrorDto.builder()
                        .rowNumber(error.getRowNumber())
                        .errorType(error.getErrorType())
                        .errorMessage(error.getErrorMessage())
                        .build())
                .collect(Collectors.toList());
        
        return ClientErrorsResponseDto.builder()
                .processId(processId)
                .clientCode(clientCode)
                .errors(errorDtos)
                .build();
    }
    
    private ClientDetailDto mapToClientDetailDto(Client client) {
        Optional<Account> account = accountRepository.findByClientId(client.getId());
        
        ClientAccountDto accountDto = account.map(acc -> ClientAccountDto.builder()
                .accountNumber(acc.getAccountNumber())
                .payrollValue(acc.getPayrollValue())
                .status(acc.getStatus())
                .lastPayrollPaid(false) 
                .build())
                .orElse(null);
        
        return ClientDetailDto.builder()
                .clientCode(client.getClientCode())
                .idType(client.getIdType())
                .idNumber(client.getIdNumber())
                .firstNames(client.getFirstNames())
                .lastNames(client.getLastNames())
                .birthDate(client.getBirthDate() != null ? client.getBirthDate().format(DATE_FORMATTER) : null)
                .joinDate(client.getJoinDate() != null ? client.getJoinDate().format(DATE_FORMATTER) : null)
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .account(accountDto)
                .build();
    }
}
