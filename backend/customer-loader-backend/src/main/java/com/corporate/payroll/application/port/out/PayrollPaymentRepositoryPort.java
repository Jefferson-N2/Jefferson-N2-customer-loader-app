package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.PayrollPayment;
import java.util.List;

public interface PayrollPaymentRepositoryPort {
    
    /**
     * Guarda un pago de nómina
     */
    void save(PayrollPayment payment);
    
    /**
     * Obtiene todos los pagos de una cuenta por número de cuenta
     */
    List<PayrollPayment> findByAccountNumber(String accountNumber);
    
    /**
     * Obtiene pagos de una cuenta por número de cuenta con paginación
     */
    List<PayrollPayment> findByAccountNumber(String accountNumber, int page, int size);
    
    /**
     * Obtiene todos los pagos con paginación
     */
    List<PayrollPayment> findAll(int page, int size);
    
    /**
     * Cuenta el total de pagos
     */
    long countAll();
    
    /**
     * Obtiene pagos por ID de cuenta con paginación
     */
    List<PayrollPayment> findByAccountId(Long accountId, int page, int size);
    
    /**
     * Cuenta pagos por ID de cuenta
     */
    long countByAccountId(Long accountId);
}