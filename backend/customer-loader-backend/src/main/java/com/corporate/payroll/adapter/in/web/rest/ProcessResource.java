package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.adapter.in.web.service.PaginationService;
import com.corporate.payroll.adapter.in.web.dto.ProcessDetailsResponseDto;
import com.corporate.payroll.adapter.in.web.dto.PagedResponseDto;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.application.port.out.ClientRepositoryPort;
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

@ApplicationScoped
@Path("/processes")
public class ProcessResource {

    @Inject
    private BulkLoadProcessRepositoryPort processRepository;

    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;

    @Inject
    private ClientRepositoryPort clientRepository;
    
    @Inject
    private PaginationService paginationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProcesses(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<BulkLoadProcess> processes = processRepository.findAll(page, size);
        long totalElements = processRepository.countAll();
        
        PagedResponseDto<BulkLoadProcess> response = paginationService.createPagedResponse(
                processes, totalElements, page, size);
        
        return Response.ok(response).build();
    }

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

    @GET
    @Path("/{processId}/errors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessErrors(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size) {

        List<BulkLoadError> errors = errorRepository.findByProcessId(processId, page, size);
        long totalErrors = errorRepository.countByProcessId(processId);
        
        PagedResponseDto<BulkLoadError> response = paginationService.createPagedResponse(
                errors, totalErrors, page, size);

        return Response.ok(response).build();
    }

    @GET
    @Path("/{processId}/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProcessClients(
            @PathParam("processId") String processId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size) {

        Optional<BulkLoadProcess> process = processRepository.findByProcessId(processId);

        if (process.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Proceso no encontrado\"}")
                    .build();
        }

        List<Client> clients = clientRepository.findByProcessId(processId, page, size);
        long totalClients = clientRepository.countByProcessId(processId);
        
        PagedResponseDto<Client> response = paginationService.createPagedResponse(
                clients, totalClients, page, size);

        return Response.ok(response).build();
    }
}