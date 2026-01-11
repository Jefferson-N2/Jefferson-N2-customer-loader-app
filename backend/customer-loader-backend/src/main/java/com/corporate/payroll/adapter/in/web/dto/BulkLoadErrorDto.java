package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO que representa un error encontrado durante la carga masiva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadErrorDto {
    
    private String idType;
    private String idNumber;
    private Integer rowNumber;
    private String errorMessage;
    private String errorType;
    private LocalDateTime processingDate;
}
