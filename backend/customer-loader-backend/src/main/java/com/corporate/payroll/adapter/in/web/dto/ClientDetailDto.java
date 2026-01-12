package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailDto {
    
    // Client info
    private String clientCode;
    private String idType;
    private String idNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate joinDate;
    private LocalDate birthDate;
    
    // Account and payment as separate DTOs
    private AccountDetailDto account;
    private FirstPaymentDto firstPayment;
}