package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailDto {
    private Long id;
    private String accountNumber;
    private String payrollValue;
    private String status;
    private List<PayrollPaymentResponseDto> payments;
}
