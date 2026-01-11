package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para consultas de errores de carga masiva
 * 
 * Endpoints:
 * - GET /errors/{processId}: Errores de un proceso específico
 */
@ApplicationScoped
@Path("/errors")
public class ErrorResource {

    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;

    /**
     * GET /errors/{processId}
     * Obtiene los errores de un proceso específico
     * 
     * @param processId ID del proceso de carga
     * @param page número de página
     * @param size tamaño de página
     * @return Lista de errores del proceso
     */
    @GET
    @Path("/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessErrors(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        return Response.ok(errorRepository.findByProcessId(processId, page, size)).build();
    }
}
