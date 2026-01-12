package com.corporate.payroll.application.port.out;

import com.corporate.payroll.domain.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepositoryPort {

    /**
     * Guarda un cliente en la base de datos y retorna el cliente con ID asignado
     */
    Client save(Client client);

    /**
     * Verifica si existe un cliente con el número de identificación especificado.
     * Esto es crítico para prevenir duplicados en la carga masiva.
     */
    boolean existsByIdNumber(String idNumber);
    
    /**
     * Obtiene un cliente por su código
     */
    Optional<Client> findByClientCode(String clientCode);
    
    /**
     * Obtiene todos los clientes (paginado)
     */
    List<Client> findAll(int page, int size);
    
    /**
     * Cuenta el total de clientes
     */
    long countAll();
    
    /**
     * Obtiene clientes por processId (paginado)
     */
    List<Client> findByProcessId(String processId, int page, int size);
    
    /**
     * Cuenta el total de clientes para un processId
     */
    long countByProcessId(String processId);
}