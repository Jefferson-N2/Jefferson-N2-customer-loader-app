package com.corporate.payroll.adapter.out.external;

import com.corporate.payroll.application.port.out.DatabookPort;
import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Implementación del puerto de salida DatabookPort.
 * Simula la consulta a un servicio externo de demografía.
 */
@ApplicationScoped
public class DatabookAdapter implements DatabookPort {

    private final DatabookService databookService;

    @Inject
    public DatabookAdapter(DatabookService databookService) {
        this.databookService = databookService;
    }

    @Override
    public Optional<DatabookResponseDto> getClientInfo(String idType, String idNumber) {

        return databookService.getClientInfo(idType,idNumber);

    }
}
