package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.in.web.rest.api.ErrorApiInputPort;
import com.corporate.payroll.application.port.in.BulkLoadQueryPort;
import com.corporate.payroll.adapter.in.web.dto.ClientErrorsResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para consultas de errores de carga masiva
 * 
 * Endpoints:
 * - GET /bulk-load/clients/{processId}/clients/{clientCode}/errors: Errores de un cliente
 */
@ApplicationScoped
@Path("/bulk-load/clients/{processId}/clients/{clientCode}/errors")
public class ErrorResource implements ErrorApiInputPort {

    @Inject
    private BulkLoadQueryPort bulkLoadQueryPort;

    /**
     * GET /bulk-load/clients/{processId}/clients/{clientCode}/errors
     * Obtiene los errores asociados a un cliente específico dentro de un proceso
     * 
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return Lista de errores del cliente
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getClientErrors(
            @PathParam("processId") String processId,
            @PathParam("clientCode") String clientCode) {
        
        ClientErrorsResponseDto response = bulkLoadQueryPort.getClientErrors(processId, clientCode);
        return Response.ok(response).build();
    }
}
