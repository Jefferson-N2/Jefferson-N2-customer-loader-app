package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadErrorResponseDto;
import java.util.List;

/**
 * Puerto de entrada (Use Case Interface) para la consulta de errores de carga masiva.
 * Define el contrato para recuperar errores de procesamiento de archivos.
 */
public interface BulkLoadErrorPort {
    
    /**
     * Obtiene los errores asociados a un código de cliente específico.
     * 
     * @param clientCode código del cliente
     * @return lista de errores del cliente
     */
    List<BulkLoadErrorResponseDto> getErrorsByClientCode(String clientCode);
    
    /**
     * Obtiene los errores asociados a un número de identificación específico.
     * 
     * @param idNumber número de identificación
     * @return lista de errores del número de identificación
     */
    List<BulkLoadErrorResponseDto> getErrorsByIdNumber(String idNumber);
    
    /**
     * Obtiene los errores de una fila específica del archivo procesado.
     * 
     * @param rowNumber número de fila
     * @return lista de errores de la fila
     */
    List<BulkLoadErrorResponseDto> getErrorsByRowNumber(Integer rowNumber);
}
