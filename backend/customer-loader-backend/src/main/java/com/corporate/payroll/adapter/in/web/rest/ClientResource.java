package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.adapter.in.web.service.PaginationService;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.adapter.in.web.dto.PagedResponseDto;
import com.corporate.payroll.domain.model.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Tag(name = "Clientes", description = "Operaciones relacionadas con clientes")
@ApplicationScoped
@Path("/clients")
public class ClientResource {

    @Inject
    private ClientRepositoryPort clientRepository;
    
    @Inject
    private PaginationService paginationService;

    @Operation(
        summary = "Listar todos los clientes",
        description = "Obtiene todos los clientes registrados con paginación"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista paginada de clientes",
        content = @Content(mediaType = "application/json")
    )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClients(
            @Parameter(description = "Número de página (0-indexed)") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Tamaño de página") @QueryParam("size") @DefaultValue("20") int size) {
        
        List<Client> clients = clientRepository.findAll(page, size);
        long totalElements = clientRepository.countAll();
        
        PagedResponseDto<Client> response = paginationService.createPagedResponse(
                clients, totalElements, page, size);
        
        return Response.ok(response).build();
    }

    @GET
    @Path("/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientsByProcess(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<Client> clients = clientRepository.findByProcessId(processId, page, size);
        long totalElements = clientRepository.countByProcessId(processId);
        
        PagedResponseDto<Client> response = paginationService.createPagedResponse(
                clients, totalElements, page, size);
        
        return Response.ok(response).build();
    }

    @GET
    @Path("/code/{clientCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientByCode(@PathParam("clientCode") String clientCode) {
        
        Optional<Client> clientOpt = clientRepository.findByClientCode(clientCode);
        
        if (clientOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Cliente no encontrado\"}")
                    .build();
        }
        
        return Response.ok(clientOpt.get()).build();
    }
}