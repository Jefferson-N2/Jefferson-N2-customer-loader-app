package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;

public interface ClientDetailQueryPort {
    
    /**
     * Obtiene el detalle de un cliente específico incluida su cuenta
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return Detalle del cliente con información de cuenta
     */
    ClientDetailResponseDto getClientDetail(String processId, String clientCode);
}
