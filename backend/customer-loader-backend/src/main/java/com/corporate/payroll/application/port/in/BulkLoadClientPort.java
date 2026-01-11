package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import java.io.InputStream;

/**
 * Puerto de entrada (Use Case Interface) para la carga masiva de clientes.
 * Define el contrato para procesar archivos de carga masiva de clientes.
 */
public interface BulkLoadClientPort {
    
    /**
     * Procesa un archivo de carga masiva de clientes.
     * 
     * @param inputStream flujo de entrada del archivo a procesar
     * @return estadísticas del procesamiento
     * @throws IllegalArgumentException si los datos son inválidos
     */
    BulkLoadStatisticsResponseDto processBulkLoad(InputStream inputStream);
}
