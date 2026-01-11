package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.PayrollPayment;
import java.util.List;

public interface PayrollPaymentRepositoryPort {
    
    /**
     * Guarda un pago de nómina
     */
    PayrollPayment save(PayrollPayment payment);
    
    /**
     * Obtiene todos los pagos de una cuenta
     */
    List<PayrollPayment> findByAccountId(Long accountId);
}
