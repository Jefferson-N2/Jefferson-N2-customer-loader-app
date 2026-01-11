package com.corporate.payroll.application.port.in.web.rest.api;

import jakarta.ws.rs.core.Response;

/**
 * Interfaz para consultas de clientes dentro de un proceso de carga
 */
public interface ClientApiInputPort {
    
    /**
     * Obtiene lista de clientes creados en un proceso
     * @param processId ID del proceso de carga
     * @param page número de página
     * @param size cantidad por página
     * @return lista paginada de clientes
     */
    Response getLoadedClients(String processId, int page, int size);
    
    /**
     * Obtiene detalle de un cliente específico
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return detalle del cliente con su cuenta
     */
    Response getClientDetail(String processId, String clientCode);
}
