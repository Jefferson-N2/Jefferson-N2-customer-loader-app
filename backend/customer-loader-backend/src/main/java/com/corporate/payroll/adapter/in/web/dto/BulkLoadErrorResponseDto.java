package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de errores de carga masiva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadErrorResponseDto {
    private Long id;
    private String clientCode;
    private String idType;
    private String idNumber;
    private Integer rowNumber;
    private String errorMessage;
    private String errorType;
    private LocalDateTime createdAt;
}
