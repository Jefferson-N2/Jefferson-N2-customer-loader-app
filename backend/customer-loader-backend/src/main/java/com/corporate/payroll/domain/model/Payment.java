package com.corporate.payroll.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    
    private Long id;
    private String accountNumber;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String status;
}
