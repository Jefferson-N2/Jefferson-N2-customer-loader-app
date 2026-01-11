package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.ClientsPageResponseDto;
import com.corporate.payroll.adapter.in.web.dto.ClientErrorsResponseDto;

/**
 * Puerto de entrada para consultar datos de clientes cargados
 */
public interface BulkLoadQueryPort {
    
    /**
     * Obtiene los clientes creados en un proceso de carga específico (paginado)
     */
    ClientsPageResponseDto getClientsByProcess(String processId, int page, int size);
    
    /**
     * Obtiene los errores asociados a un cliente específico dentro de un proceso de carga
     */
    ClientErrorsResponseDto getClientErrors(String processId, String clientCode);
}
