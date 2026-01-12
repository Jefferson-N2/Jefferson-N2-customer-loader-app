package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
import com.corporate.payroll.adapter.in.web.dto.ProcessDetailsResponseDto;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.domain.model.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
@Path("/processes")
public class ProcessResource {

    @Inject
    private BulkLoadProcessRepositoryPort processRepository;
    
    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;
    
    @Inject
    private ClientRepositoryPort clientRepository;

    /**
     * GET /processes
     * Obtiene todos los archivos/procesos cargados paginados
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProcesses(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<BulkLoadProcess> processes = processRepository.findAll(page, size);
        long totalElements = processRepository.countAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", processes);
        response.put("totalElements", totalElements);
        response.put("totalPages", (int) Math.ceil((double) totalElements / size));
        response.put("size", size);
        response.put("number", page);
        response.put("empty", processes.isEmpty());
        
        return Response.ok(response).build();
    }

    /**
     * GET /processes/{processId}/details
     * Obtiene detalles completos del proceso incluyendo errores y clientes
     */
    @GET
    @Path("/{processId}/details")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessDetails(
            @PathParam("processId") String processId,
            @QueryParam("includeErrors") @DefaultValue("true") boolean includeErrors) {

        Optional<BulkLoadProcess> process = processRepository.findByProcessId(processId);

        if (process.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Proceso no encontrado\"}")
                    .build();
        }

        BulkLoadProcess processData = process.get();
        List<Client> clients = clientRepository.findByProcessId(processId, 0, 100);
        List<BulkLoadError> errors = null;
        
        if (includeErrors && processData.getErrorCount() != null && processData.getErrorCount() > 0) {
            errors = errorRepository.findByProcessId(processId);
        }
        
        ProcessDetailsResponseDto response = ProcessDetailsResponseDto.builder()
                .processId(processData.getProcessId())
                .fileName(processData.getFileName())
                .status(processData.getStatus())
                .totalRecords(processData.getTotalRecords())
                .successfulCount(processData.getSuccessfulCount())
                .errorCount(processData.getErrorCount())
                .processingDate(processData.getProcessingDate())
                .clients(clients)
                .errors(errors)
                .build();

        return Response.ok(response).build();
    }

    /**
     * GET /processes/{processId}/errors
     * Obtiene solo los errores de un proceso
     */
    @GET
    @Path("/{processId}/errors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessErrors(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {

        List<BulkLoadError> errors;
        if (page == 0 && size == 50) {
            errors = errorRepository.findByProcessId(processId);
        } else {
            errors = errorRepository.findByProcessId(processId, page, size);
        }

        return Response.ok(errors).build();
    }

    /**
     * GET /processes/{processId}/clients
     * Obtiene los clientes procesados exitosamente
     */
    @GET
    @Path("/{processId}/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessClients(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        Optional<BulkLoadProcess> process = processRepository.findByProcessId(processId);

        if (process.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Proceso no encontrado\"}")
                    .build();
        }

        List<Client> clients = clientRepository.findByProcessId(processId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("processId", processId);
        response.put("clients", clients);
        response.put("totalClients", clients.size());

        return Response.ok(response).build();
    }
}