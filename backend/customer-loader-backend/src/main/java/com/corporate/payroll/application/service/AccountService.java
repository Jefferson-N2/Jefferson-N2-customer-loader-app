package com.corporate.payroll.application.service;

import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.port.out.PayrollPaymentRepositoryPort;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.PayrollPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AccountService {

    @Inject
    private AccountRepositoryPort accountRepository;
    
    @Inject
    private PayrollPaymentRepositoryPort paymentRepository;

    public Account findAccountByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId)
                .orElseThrow(() -> new BusinessLogicException("Cuenta no encontrada"));
    }

    public PayrollPayment findFirstPaymentByAccountId(Long accountId) {
        return paymentRepository.findFirstPaymentByAccountId(accountId)
                .orElseThrow(() -> new BusinessLogicException("Pago no encontrada"));
    }
}