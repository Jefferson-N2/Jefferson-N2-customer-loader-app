package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.in.web.rest.api.BulkLoadApiInputPort;
import com.corporate.payroll.adapter.in.web.security.SecurityValidationInterceptor;
import com.corporate.payroll.application.port.in.BulkLoadClientUseCase;
import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import com.corporate.payroll.adapter.in.web.dto.BulkLoadResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;


@ApplicationScoped
@Path("/bulk-load/clients")
public class BulkLoadClientResource implements BulkLoadApiInputPort {

    @Inject
    private BulkLoadClientUseCase bulkLoadUseCase;

    /**
     * POST /bulk-load/clients
     * Procesa un archivo de carga masiva de clientes en línea
     * 
     * @param inputStream stream del archivo
     * @param fileName nombre del archivo
     * @return Respuesta con processId, status y estadísticas
     */
    @POST
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM})
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response uploadClients(
            InputStream inputStream,
            @QueryParam("fileName") String fileName) {
        
       
        fileName = SecurityValidationInterceptor.sanitizeFileName(fileName);
        
        BulkLoadStatisticsResponseDto stats = bulkLoadUseCase.processBulkLoad(inputStream, fileName);
        
        BulkLoadResponseDto response = BulkLoadResponseDto.builder()
                .processId(stats.getProcessId())
                .status("COMPLETED")
                .successCount(stats.getSuccessfulCount())
                .errorCount(stats.getErrorCount())
                .message("Carga finalizada")
                .build();
        
        return Response.ok(response).build();
    }
}
