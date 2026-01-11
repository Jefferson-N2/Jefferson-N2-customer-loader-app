package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para la respuesta paginada de clientes creados exitosamente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientsPageResponseDto {
    private String processId;
    private int page;
    private int pageSize;
    private int totalPages;
    private List<ClientDetailDto> clients;
}
