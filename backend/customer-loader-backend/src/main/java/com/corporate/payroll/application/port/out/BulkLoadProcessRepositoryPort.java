package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.BulkLoadProcess;
import java.util.Optional;

public interface BulkLoadProcessRepositoryPort {
    
    /**
     * Guarda un nuevo proceso de carga masiva
     * @param bulkLoadProcess el proceso a guardar
     * @return el proceso guardado
     */
    BulkLoadProcess save(BulkLoadProcess bulkLoadProcess);
    
    /**
     * Busca un proceso por su processId
     * @param processId el ID del proceso
     * @return Optional con el proceso si existe
     */
    Optional<BulkLoadProcess> findByProcessId(String processId);
    
    /**
     * Actualiza un proceso existente
     * @param bulkLoadProcess el proceso a actualizar
     * @return el proceso actualizado
     */
    BulkLoadProcess update(BulkLoadProcess bulkLoadProcess);
}
