package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.in.web.rest.api.ClientApiInputPort;
import com.corporate.payroll.application.port.in.BulkLoadQueryPort;
import com.corporate.payroll.application.port.in.ClientDetailQueryPort;
import com.corporate.payroll.adapter.in.web.dto.ClientsPageResponseDto;
import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para consultas de clientes en procesos de carga masiva
 * 
 * Endpoints:
 * - GET /bulk-load/clients/{processId}/clients: Lista de clientes (paginado)
 * - GET /bulk-load/clients/{processId}/clients/{clientCode}: Detalle de cliente
 */
@ApplicationScoped
@Path("/bulk-load/clients/{processId}/clients")
public class ClientResource implements ClientApiInputPort {

    @Inject
    private BulkLoadQueryPort bulkLoadQueryPort;
    
    @Inject
    private ClientDetailQueryPort clientDetailQueryPort;

    /**
     * GET /bulk-load/clients/{processId}/clients
     * Obtiene los clientes creados exitosamente en un proceso de carga (paginado)
     * 
     * @param processId ID del proceso de carga
     * @param page número de página (desde 0)
     * @param size cantidad de registros por página
     * @return Lista paginada de clientes con sus cuentas
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getLoadedClients(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        ClientsPageResponseDto response = bulkLoadQueryPort.getClientsByProcess(processId, page, size);
        return Response.ok(response).build();
    }

    /**
     * GET /bulk-load/clients/{processId}/clients/{clientCode}
     * Obtiene el detalle de un cliente específico incluida su cuenta
     * 
     * @param processId ID del proceso de carga
     * @param clientCode código del cliente
     * @return Detalle del cliente con información de cuenta
     */
    @GET
    @Path("/{clientCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getClientDetail(
            @PathParam("processId") String processId,
            @PathParam("clientCode") String clientCode) {
        
        ClientDetailResponseDto response = clientDetailQueryPort.getClientDetail(processId, clientCode);
        return Response.ok(response).build();
    }
}

