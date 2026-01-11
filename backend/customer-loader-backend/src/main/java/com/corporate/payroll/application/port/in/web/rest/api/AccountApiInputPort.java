package com.corporate.payroll.application.port.in.web.rest.api;

import jakarta.ws.rs.core.Response;

/**
 * Interfaz para consultas de cuentas y pagos
 */
public interface AccountApiInputPort {
    
    /**
     * Obtiene historial de pagos de una cuenta
     * @param accountNumber número de cuenta
     * @param page número de página
     * @param size cantidad por página
     * @return historial paginado de pagos
     */
    Response getAccountPayments(String accountNumber, int page, int size);
}
