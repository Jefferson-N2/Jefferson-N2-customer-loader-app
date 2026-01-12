package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.application.service.AccountService;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.PayrollPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
@Path("/accounts")
public class AccountResource {

    @Inject
    private AccountService accountService;

    @GET
    @Path("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByClientId(@PathParam("clientId") Long clientId) {


        return Response.ok(accountService.findAccountByClientId(clientId)).build();
    }

    @GET
    @Path("/client/{accountId}/first-payment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFirstPaymentByClientId(@PathParam("accountId") Long accountId) {


        return Response.ok(accountService.findFirstPaymentByAccountId(accountId)).build();
    }
}