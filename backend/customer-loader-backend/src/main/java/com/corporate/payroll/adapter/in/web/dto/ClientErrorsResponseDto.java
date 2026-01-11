package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para la respuesta de errores de un cliente específico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientErrorsResponseDto {
    private String processId;
    private String clientCode;
    private List<ClientErrorDto> errors;
}
