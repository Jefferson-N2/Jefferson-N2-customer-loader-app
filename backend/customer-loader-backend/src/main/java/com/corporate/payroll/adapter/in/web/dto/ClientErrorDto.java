package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un error asociado a un cliente específico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientErrorDto {
    private Integer rowNumber;
    private String errorType;
    private String errorMessage;
}
