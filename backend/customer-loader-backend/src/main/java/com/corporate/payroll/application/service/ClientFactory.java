package com.corporate.payroll.application.service;

import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.application.util.FileProcessingConstants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Factory CDI para generar códigos únicos de cliente.
 * Consulta la base de datos para verificar unicidad en lugar de mantener cache en memoria.
 */
@ApplicationScoped
@Slf4j
public class ClientFactory {
    
    private static final String CLIENT_CODE_PREFIX = FileProcessingConstants.DEFAULT_CLIENT_CODE_PREFIX;
    
    @Inject
    private ClientRepositoryPort clientRepository;
    
    /**
     * Genera un código de cliente único consultando la base de datos.
     * Retorna null si no puede generar después de 10 intentos.
     * 
     * @return código único de cliente o null si falla
     */
    public String generateUniqueClientCode() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;
        
        while (attempts < MAX_ATTEMPTS) {
            String code = CLIENT_CODE_PREFIX + "_" 
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
            
            if (clientRepository.findByClientCode(code).isEmpty()) {
                log.debug("Código de cliente generado: {}", code);
                return code;
            }
            
            attempts++;
            log.warn("Código {} ya existe, reintentando (intento {}/{})", code, attempts, MAX_ATTEMPTS);
        }
        
        log.error("No se pudo generar un código de cliente único después de {} intentos", MAX_ATTEMPTS);
        return null;
    }
}