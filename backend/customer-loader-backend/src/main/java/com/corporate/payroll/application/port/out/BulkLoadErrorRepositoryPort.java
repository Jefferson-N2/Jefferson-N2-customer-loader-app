package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.BulkLoadError;

import java.util.List;

/**
 * Puerto de salida para persistir y consultar errores de carga masiva.
 */
public interface BulkLoadErrorRepositoryPort {
    
    /**
     * Persiste una Lista de errores de carga masiva
     * @param errors el error a persistir
     */
    void saveAll(List<BulkLoadError> errors);
    
    /**
     * Busca todos los errores de un proceso específico
     * @param processId ID del proceso de carga
     * @return lista de errores del proceso
     */
    List<BulkLoadError> findByProcessId(String processId);
    
    /**
     * Busca todos los errores de un proceso específico (paginado)
     * @param processId ID del proceso de carga
     * @param page número de página
     * @param size tamaño de página
     * @return lista de errores del proceso
     */
    List<BulkLoadError> findByProcessId(String processId, int page, int size);
    
    /**
     * Cuenta el total de errores de un proceso
     * @param processId ID del proceso de carga
     * @return número total de errores
     */
    long countByProcessId(String processId);
}
