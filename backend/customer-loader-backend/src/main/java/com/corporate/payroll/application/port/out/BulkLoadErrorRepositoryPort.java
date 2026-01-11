package com.corporate.payroll.application.port.out;

import com.corporate.payroll.adapter.in.web.dto.FileSummaryResponseDto;
import com.corporate.payroll.domain.model.BulkLoadError;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Puerto de salida para persistir y consultar errores de carga masiva.
 * Soporta búsqueda de resúmenes de archivos y errores detallados por archivo.
 */
public interface BulkLoadErrorRepositoryPort {
    
    /**
     * Persiste una Lista de errores de carga masiva
     * @param errors el error a persistir
     */
    void saveAll(List<BulkLoadError> errors);

    /**
     * Busca todos los errores de un archivo específico (paginado).
     * El nombre del archivo incluye timestamp en formato: ddmmaaahh:mm:ss
     * @param fileName nombre del archivo
     * @param offset desplazamiento para paginación
     * @param limit cantidad de registros por página
     * @return lista de errores encontrados
     */
    List<BulkLoadError> findByFileName(String fileName, long offset, int limit);
    
    /**
     * Busca todos los errores asociados a un cliente específico
     * @param clientCode código del cliente
     * @return lista de errores del cliente
     */
    List<BulkLoadError> findByClientCode(String clientCode);
    
    /**
     * Busca todos los errores asociados a un cliente en un proceso específico
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return lista de errores del cliente en el proceso
     */
    List<BulkLoadError> findByClientCodeAndProcessId(String processId, String clientCode);
}
