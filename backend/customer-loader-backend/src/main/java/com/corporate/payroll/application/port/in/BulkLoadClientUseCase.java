package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import java.io.InputStream;

/**
 * Caso de uso: Carga masiva de clientes
 * Puerto de entrada para procesamiento de archivos TXT con datos de clientes
 */
public interface BulkLoadClientUseCase {
    
    /**
     * Procesa la carga masiva de clientes desde un archivo TXT.
     * Valida cada fila usando ClientValidator del dominio.
     * Captura el nombre del archivo para trazabilidad en errores.
     * 
     * @param fileStream Stream del archivo a procesar
     * @param fileName Nombre del archivo para trazabilidad en errores
     * @return Respuesta con estadísticas de carga (exitosos y errores)
     */
    BulkLoadStatisticsResponseDto processBulkLoad(InputStream fileStream, String fileName);
}
