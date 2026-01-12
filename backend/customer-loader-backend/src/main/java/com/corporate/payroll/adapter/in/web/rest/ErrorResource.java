package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.adapter.in.web.service.PaginationService;
import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.adapter.in.web.dto.PagedResponseDto;
import com.corporate.payroll.domain.model.BulkLoadError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/errors")
public class ErrorResource {

    @Inject
    private BulkLoadErrorRepositoryPort errorRepository;
    
    @Inject
    private PaginationService paginationService;

    @GET
    @Path("/{processId}")
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
}
