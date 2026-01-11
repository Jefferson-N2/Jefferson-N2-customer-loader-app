package com.corporate.payroll.application.port.out;

import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import java.util.Optional;

/**
 * Puerto de salida para el servicio externo Databook.
 * Define el contrato para consultar información de clientes
 * en un servicio externo de demografía.
 */
public interface DatabookPort {
    
    /**
     * Consulta información del cliente en el servicio databook.
     * Este es un servicio externo que proporciona datos demográficos.
     * 
     * @param idType Tipo de identificación (C o P)
     * @param idNumber Número de identificación
     * @return Optional con la información del cliente
     */
    Optional<DatabookResponseDto> getClientInfo(String idType, String idNumber);
}
