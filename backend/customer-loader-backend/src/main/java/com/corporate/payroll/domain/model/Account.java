package com.corporate.payroll.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    
    private Long id;
    private String accountNumber;
    private Long clientId;
    private BigDecimal payrollValue;
    private String status;
    private List<PayrollPayment> payments;
}
