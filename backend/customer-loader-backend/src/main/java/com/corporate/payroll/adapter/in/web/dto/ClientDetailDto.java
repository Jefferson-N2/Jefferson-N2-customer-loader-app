package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa los detalles de un cliente creado exitosamente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailDto {
    private String clientCode;
    private String idType;
    private String idNumber;
    private String firstNames;
    private String lastNames;
    private String birthDate;
    private String joinDate;
    private String email;
    private String phoneNumber;
    private ClientAccountDto account;
}
