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
public class PaymentPageResponseDto {
    
    private String accountNumber;
    private int page;
    private int pageSize;
    private int totalPages;
    private List<PaymentDto> payments;
}
