package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.PayrollPayment;

import java.util.Optional;
import java.util.List;

public interface PayrollPaymentOutPort {
    PayrollPayment save(PayrollPayment payment);
    Optional<PayrollPayment> findById(Long id);
    List<PayrollPayment> findByAccountId(Long accountId);
    List<PayrollPayment> findAll();
}
