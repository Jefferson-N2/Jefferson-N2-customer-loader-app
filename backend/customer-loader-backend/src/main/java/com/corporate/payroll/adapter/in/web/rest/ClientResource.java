package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.domain.model.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Recurso REST para consultas de clientes
 * 
 * Endpoints:
 * - GET /clients/{processId}: Lista de clientes por proceso
 * - GET /clients/code/{clientCode}: Detalle de cliente por código
 */
@ApplicationScoped
@Path("/clients")
public class ClientResource {

    @Inject
    private ClientRepositoryPort clientRepository;

    /**
     * GET /clients/{processId}
     * Obtiene los clientes de un proceso específico
     * 
     * @param processId ID del proceso de carga
     * @param page número de página
     * @param size tamaño de página
     * @return Lista de clientes del proceso
     */
    @GET
    @Path("/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientsByProcess(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<Client> clients = clientRepository.findByProcessId(processId, page, size);
        return Response.ok(clients).build();
    }

    /**
     * GET /clients/code/{clientCode}
     * Obtiene el detalle de un cliente por su código
     * 
     * @param clientCode código del cliente
     * @return Detalle del cliente
     */
    @GET
    @Path("/code/{clientCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientByCode(@PathParam("clientCode") String clientCode) {
        
        Optional<Client> client = clientRepository.findByClientCode(clientCode);
        
        if (client.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Cliente no encontrado\"}")
                    .build();
        }
        
        return Response.ok(client.get()).build();
    }
}

