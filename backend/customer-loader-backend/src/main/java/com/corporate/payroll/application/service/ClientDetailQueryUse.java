package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.*;
import com.corporate.payroll.application.port.in.ClientDetailQueryPort;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.PaymentRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.Payment;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para consultas de detalle de clientes y pagos
 */
@ApplicationScoped
public class ClientDetailQueryUse implements ClientDetailQueryPort {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final ClientRepositoryPort clientRepository;
    private final AccountRepositoryPort accountRepository;
    private final PaymentRepositoryPort paymentRepository;
    
    @Inject
    public ClientDetailQueryUse(ClientRepositoryPort clientRepository,
                                AccountRepositoryPort accountRepository,
                                PaymentRepositoryPort paymentRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }
    
    /**
     * Obtiene el detalle de un cliente incluida su cuenta y estado de pago
     */
    @Override
    public ClientDetailResponseDto getClientDetail(String processId, String clientCode) {
        Optional<Client> clientOpt = clientRepository.findByClientCode(clientCode);
        
        if (clientOpt.isEmpty() || !processId.equals(clientOpt.get().getProcessId())) {
            throw new BusinessLogicException("Cliente no encontrado en el proceso especificado");
        }
        
        Client client = clientOpt.get();
        Optional<Account> accountOpt = accountRepository.findByClientId(client.getId());
        
        ClientAccountDto accountDto = accountOpt.map(acc -> ClientAccountDto.builder()
                .accountNumber(acc.getAccountNumber())
                .payrollValue(acc.getPayrollValue())
                .status(acc.getStatus())
                .lastPayrollPaid(hasPayments(acc.getAccountNumber()))
                .build())
                .orElse(null);
        
        return ClientDetailResponseDto.builder()
                .processId(processId)
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
    
    /**
     * Obtiene el historial de pagos de una cuenta (paginado)
     */
    @Override
    public PaymentPageResponseDto getAccountPayments(String accountNumber, int page, int size) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        
        if (accountOpt.isEmpty()) {
            throw new BusinessLogicException("Cuenta no encontrada");
        }
        
        long totalPayments = paymentRepository.countByAccountNumber(accountNumber);
        int totalPages = (int) Math.ceil((double) totalPayments / size);
        
        List<Payment> payments = paymentRepository.findByAccountNumber(accountNumber, page * size, size);
        List<PaymentDto> paymentDtos = payments.stream()
                .map(p -> PaymentDto.builder()
                        .paymentId(p.getId())
                        .date(p.getPaymentDate().format(DATE_FORMATTER))
                        .amount(p.getAmount())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());
        
        return PaymentPageResponseDto.builder()
                .accountNumber(accountNumber)
                .page(page)
                .pageSize(size)
                .totalPages(totalPages)
                .payments(paymentDtos)
                .build();
    }
    
    /**
     * Verifica si una cuenta tiene al menos un pago registrado
     */
    private boolean hasPayments(String accountNumber) {
        return paymentRepository.countByAccountNumber(accountNumber) > 0;
    }
}
