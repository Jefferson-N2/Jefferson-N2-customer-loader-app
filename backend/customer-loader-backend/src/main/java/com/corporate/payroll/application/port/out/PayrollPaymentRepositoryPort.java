package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.PayrollPayment;
import java.util.List;
import java.util.Optional;

public interface PayrollPaymentRepositoryPort {
    
    /**
     * Guarda un pago de nómina
     */
    void save(PayrollPayment payment);
    

    Optional<PayrollPayment> findFirstPaymentByAccountId(Long acountId);

}