package com.corporate.payroll.application.service;

import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.util.FileProcessingConstants;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Factory CDI para generar números únicos de cuenta.
 * Consulta la base de datos para verificar unicidad en lugar de mantener cache en memoria.
 */
@ApplicationScoped
@Slf4j
public class AccountFactory {
    
    private static final String ACCOUNT_CODE_PREFIX = FileProcessingConstants.DEFAULT_ACCOUNT_CODE_PREFIX;
    
   @Inject
    private AccountRepositoryPort accountRepository;
    
    /**
     * Genera un número de cuenta único basado en secuencia numérica.
     * Consulta el último número usado y genera el siguiente.
     * 
     * @return número único de cuenta
     */
    public String generateUniqueAccountNumber() {
        try {
            Long lastNumber = accountRepository.getLastAccountNumber();
            Long nextNumber = (lastNumber != null) ? lastNumber + 1 : 1000000000L;
            
            String accountNumber = String.valueOf(nextNumber);
            log.debug("Número de cuenta generado: {}", accountNumber);
            return accountNumber;
        } catch (Exception e) {
            log.error("Error generando número de cuenta: {}", e.getMessage(), e);
            return String.valueOf(1000000000L + (System.currentTimeMillis() % 1000000000L));
        }
    }
}
