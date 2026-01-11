package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de POST /bulk-load/clients
 * Contiene un identificador único del proceso y estadísticas de carga
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadResponseDto {
    private String processId;
    private String status;
    private int successCount;
    private int errorCount;
    private String message;
}
