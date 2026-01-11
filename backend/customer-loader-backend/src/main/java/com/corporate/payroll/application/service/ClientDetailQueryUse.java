package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.*;
import com.corporate.payroll.adapter.in.web.mapper.PaymentDtoMapper;
import com.corporate.payroll.application.port.in.ClientDetailQueryPort;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.PayrollPaymentRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.PayrollPayment;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final PayrollPaymentRepositoryPort paymentRepository;

    @Inject
    private PaymentDtoMapper paymentDtoMapper;

    @Inject
    public ClientDetailQueryUse(ClientRepositoryPort clientRepository,
                                AccountRepositoryPort accountRepository,
                                PayrollPaymentRepositoryPort paymentRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Obtiene el detalle de un cliente incluida su cuenta y estado de pago
     */
    @Override
    public ClientDetailResponseDto getClientDetail(String processId, String clientCode) {
        Client client = clientRepository.findByClientCode(clientCode)
                .filter(c -> processId.equals(c.getProcessId()))
                .orElseThrow(() -> new BusinessLogicException("Cliente no encontrado en el proceso especificado"));

        ClientAccountDto accountDto = accountRepository.findByClientId(client.getId())
                .map(acc -> ClientAccountDto.builder()
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
                .birthDate(Optional.ofNullable(client.getBirthDate())
                        .map(d -> d.format(DATE_FORMATTER))
                        .orElse(null))
                .joinDate(Optional.ofNullable(client.getJoinDate())
                        .map(d -> d.format(DATE_FORMATTER))
                        .orElse(null))
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .account(accountDto)
                .build();
    }

    /**
     * Verifica si una cuenta tiene al menos un pago registrado
     */
    private boolean hasPayments(String accountNumber) {
        return !paymentRepository.findByAccountNumber(accountNumber).isEmpty();
    }
}
