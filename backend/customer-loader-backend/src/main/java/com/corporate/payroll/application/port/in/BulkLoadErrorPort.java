package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.FileSummaryResponseDto;
import com.corporate.payroll.adapter.in.web.dto.BulkLoadErrorResponseDto;
import java.util.List;

/**
 * Puerto de entrada (Use Case Interface) para la consulta de errores de carga masiva.
 * Define el contrato para recuperar resúmenes de archivos procesados y errores detallados.
 * El nombre del archivo incluye timestamp en formato: ddmmaaahh:mm:ss
 */
public interface BulkLoadErrorPort {
    
    /**
     * Obtiene un resumen de todos los archivos procesados con paginación.
     * Devuelve un registro por archivo mostrando su estado y si posee errores.
     * Útil para mostrar como cabecera de archivos procesados.
     * 
     * @param page número de página (comienza en 0)
     * @param size cantidad de registros por página (máximo 100)
     * @return lista paginada de resúmenes de archivos
     */
    List<FileSummaryResponseDto> getAllFilesSummary(int page, int size);
    
    /**
     * Obtiene los errores detallados asociados a un nombre de archivo específico con paginación.
     * El nombre del archivo incluye timestamp: ddmmaaahh:mm:ss
     * 
     * @param fileName nombre del archivo con timestamp (ejemplo: archivo_10012600843000.txt)
     * @param page número de página (comienza en 0)
     * @param size cantidad de registros por página (máximo 100)
     * @return lista paginada de errores detallados del archivo
     */
    List<BulkLoadErrorResponseDto> getErrorsByFileName(String fileName, int page, int size);
}
