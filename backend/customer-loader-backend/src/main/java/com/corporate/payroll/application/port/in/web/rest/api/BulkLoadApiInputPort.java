package com.corporate.payroll.application.port.in.web.rest.api;

import jakarta.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Interfaz para operaciones de carga masiva de clientes
 */
public interface BulkLoadApiInputPort {
    
    /**
     * Procesa un archivo de carga masiva de clientes
     * @param inputStream stream del archivo
     * @param fileName nombre del archivo
     * @return respuesta con processId y estadísticas
     */
    Response uploadClients(InputStream inputStream, String fileName);
}
