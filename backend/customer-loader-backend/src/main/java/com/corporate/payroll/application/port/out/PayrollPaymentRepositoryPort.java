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
}