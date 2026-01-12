package com.corporate.payroll.adapter.in.web.dto;

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
public class FirstPaymentDto {
    
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String status;
}