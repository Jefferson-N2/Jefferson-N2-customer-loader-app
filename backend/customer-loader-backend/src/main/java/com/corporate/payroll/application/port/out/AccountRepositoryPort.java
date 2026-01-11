package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.Account;
import java.util.Optional;

public interface AccountRepositoryPort {
    
    /**
     * Guarda una cuenta en la base de datos
     */
    void save(Account account);
    
    /**
     * Verifica si existe una cuenta con el número especificado.
     * Garantiza la unicidad de números de cuenta durante la carga masiva.
     */
    boolean existsByAccountNumber(String accountNumber);
    
    /**
     * Obtiene una cuenta por su número
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Obtiene la cuenta de un cliente por ID de cliente
     */
    Optional<Account> findByClientId(Long clientId);
}
