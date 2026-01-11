package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.ClientResponseDto;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Use Case Interface) para la visualización de clientes.
 * Define el contrato para recuperar y listar información de clientes.
 */
public interface ViewClientPort {
    
    /**
     * Obtiene el listado paginado de todos los clientes.
     * 
     * @param page número de página (0-indexed)
     * @param size cantidad de elementos por página
     * @return lista paginada de clientes
     */
    List<ClientResponseDto> getAllClientsPaginated(int page, int size);
    
    /**
     * Obtiene los detalles completos de un cliente específico,
     * incluyendo sus cuentas y pagos asociados.
     * 
     * @param clientId identificador único del cliente
     * @return Optional con los detalles del cliente si existe
     */
    Optional<ClientResponseDto> getClientDetail(Long clientId);
}
