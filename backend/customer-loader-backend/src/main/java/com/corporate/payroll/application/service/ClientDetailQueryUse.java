package com.corporate.payroll.application.service;

import com.corporate.payroll.adapter.in.web.dto.ClientDetailDto;
import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;
import com.corporate.payroll.adapter.in.web.mapper.ClientDetailMapper;
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

/**
 * Servicio para consultas de detalle de clientes - responsabilidad única
 */
@ApplicationScoped
public class ClientDetailQueryUse implements ClientDetailQueryPort {

    @Inject
    private ClientRepositoryPort clientRepository;
    
    @Inject
    private AccountRepositoryPort accountRepository;
    
    @Inject
    private PayrollPaymentRepositoryPort paymentRepository;
    
    @Inject
    private ClientDetailMapper clientDetailMapper;

    @Override
    public ClientDetailResponseDto getClientDetail(String processId, String clientCode) {
        Client client = findClientByCodeAndProcess(processId, clientCode);
        Account account = findAccountByClient(client);
        PayrollPayment lastPayment = findLastPayment(account.getAccountNumber());
        
        ClientDetailDto clientDetail = clientDetailMapper.toDto(client, account, lastPayment);
        
        return ClientDetailResponseDto.builder()
                .processId(processId)
                .clientDetail(clientDetail)
                .build();
    }

    private Client findClientByCodeAndProcess(String processId, String clientCode) {
        return clientRepository.findByClientCode(clientCode)
                .filter(c -> processId.equals(c.getProcessId()))
                .orElseThrow(() -> new BusinessLogicException("Cliente no encontrado en el proceso especificado"));
    }

    private Account findAccountByClient(Client client) {
        return accountRepository.findByClientId(client.getId())
                .orElseThrow(() -> new BusinessLogicException("Cuenta no encontrada para el cliente"));
    }

    private PayrollPayment findLastPayment(String accountNumber) {
        return paymentRepository.findByAccountNumber(accountNumber)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
