package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;
import com.corporate.payroll.adapter.in.web.dto.PaymentPageResponseDto;

public interface ClientDetailQueryPort {
    
    /**
     * Obtiene el detalle de un cliente específico incluida su cuenta
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return Detalle del cliente con información de cuenta
     */
    ClientDetailResponseDto getClientDetail(String processId, String clientCode);
    
    /**
     * Obtiene el historial de pagos de una cuenta (paginado)
     * @param accountNumber número de cuenta
     * @param page número de página (desde 0)
     * @param size cantidad de registros por página
     * @return Respuesta paginada con historial de pagos
     */
    PaymentPageResponseDto getAccountPayments(String accountNumber, int page, int size);
}
