package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollPaymentResponseDto {
    private Long id;
    private String paymentDate;
    private String amount;
    private String status;
}
