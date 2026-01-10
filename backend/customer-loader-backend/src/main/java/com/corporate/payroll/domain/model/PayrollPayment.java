package com.corporate.payroll.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollPayment {
    
    private Long id;
    private Long accountId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String status;
}
