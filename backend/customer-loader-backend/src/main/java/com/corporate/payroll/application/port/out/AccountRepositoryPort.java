package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.Account;
import java.util.Optional;
import java.util.List;

public interface AccountRepositoryPort {
    
    /**
     * Guarda una cuenta en la base de datos
     */
    void save(Account account);

    /**
     * Busca una cuenta por su ID
     */
    Optional<Account> findById(Long id);
    
    /**
     * Busca una cuenta por su número de cuenta
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Obtiene todas las cuentas de un cliente
     */
    List<Account> findByClientId(Long clientId);
    
    /**
     * Verifica si existe una cuenta con el número especificado
     */
    boolean existsByAccountNumber(String accountNumber);
}
