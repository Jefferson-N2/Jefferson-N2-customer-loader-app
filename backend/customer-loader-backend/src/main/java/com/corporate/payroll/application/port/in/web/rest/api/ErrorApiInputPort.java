package com.corporate.payroll.application.port.in.web.rest.api;

import jakarta.ws.rs.core.Response;

/**
 * Interfaz para consultas de errores de carga masiva
 */
public interface ErrorApiInputPort {
    
    /**
     * Obtiene errores asociados a un cliente en un proceso
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return lista de errores del cliente
     */
    Response getClientErrors(String processId, String clientCode);
}
