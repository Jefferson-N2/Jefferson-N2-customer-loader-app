package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.port.in.web.rest.api.AccountApiInputPort;
import com.corporate.payroll.application.port.in.ClientDetailQueryPort;
import com.corporate.payroll.adapter.in.web.dto.PaymentPageResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para consultas de cuentas y pagos
 * 
 * Endpoints:
 * - GET /accounts/{accountNumber}/payments: Historial de pagos
 */
@ApplicationScoped
@Path("/accounts")
public class AccountResource implements AccountApiInputPort {
    
    @Inject
    private ClientDetailQueryPort clientDetailQueryPort;
    
    /**
     * GET /accounts/{accountNumber}/payments
     * Obtiene el historial de pagos de una cuenta (paginado)
     * 
     * @param accountNumber número de cuenta
     * @param page número de página (desde 0)
     * @param size cantidad de registros por página
     * @return Respuesta paginada con historial de pagos
     */
    @GET
    @Path("/{accountNumber}/payments")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAccountPayments(
            @PathParam("accountNumber") String accountNumber,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        PaymentPageResponseDto response = clientDetailQueryPort.getAccountPayments(accountNumber, page, size);
        return Response.ok(response).build();
    }
}
