package com.corporate.payroll.application.port.in;

import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;
import com.corporate.payroll.adapter.in.web.dto.ClientResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso: Visualización de clientes
 * Puerto de entrada para consultas de clientes y sus detalles
 */
public interface ViewClientUseCase {

    List<ClientResponseDto> getAllClientsPaginated(int page, int size);

    /**
     * Obtiene los detalles completos de un cliente
     * @param clientId Identificador único del cliente
     * @return Detalles del cliente si existe
     */
    Optional<ClientDetailResponseDto> getClientDetail(Long clientId);
}
