package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAccountDto {
    private String accountNumber;
    private BigDecimal payrollValue;
    private String status;
    private boolean lastPayrollPaid;
}
