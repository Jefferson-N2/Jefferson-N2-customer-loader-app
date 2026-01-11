package com.corporate.payroll.application.service;

import com.corporate.payroll.application.port.out.AccountRepositoryPort;
import com.corporate.payroll.application.util.FileProcessingConstants;
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
     * Genera un número de cuenta único consultando la base de datos.
     * Retorna null si no puede generar después de 10 intentos.
     * 
     * @return número único de cuenta o null si falla
     */
    public String generateUniqueAccountNumber() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;
        
        while (attempts < MAX_ATTEMPTS) {
            String number = ACCOUNT_CODE_PREFIX + "_" 
            + UUID.randomUUID().toString().substring(0, 10).toUpperCase(java.util.Locale.ROOT);
            
            if (accountRepository.findByAccountNumber(number).isEmpty()) {
                log.debug("Número de cuenta generado: {}", number);
                return number;
            }
            
            attempts++;
            log.warn("Número de cuenta {} ya existe, reintentando (intento {}/{})", number, attempts, MAX_ATTEMPTS);
        }
        
        log.error("No se pudo generar un número de cuenta único después de {} intentos", MAX_ATTEMPTS);
        return null;
    }
}
