package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private Long id;
    private String clientCode;
    private String idType;
    private String idNumber;
    private String firstNames;
    private String lastNames;
    private String birthDate;
    private String joinDate;
    private String email;
    private String phoneNumber;
}
