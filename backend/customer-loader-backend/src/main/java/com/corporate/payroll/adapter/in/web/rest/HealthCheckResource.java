package com.corporate.payroll.adapter.in.web.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Path("/health")
@Slf4j
public class HealthCheckResource {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "customer-loader-backend");
        health.put("version", "1.0.0");

        Map<String, String> checks = new HashMap<>();
        
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            checks.put("database", "UP");
        } catch (Exception e) {
            checks.put("database", "DOWN");
            health.put("status", "DOWN");
            log.error("Database health check failed", e);
        }

        health.put("checks", checks);
        
        int status = "UP".equals(health.get("status")) ? 200 : 503;
        return Response.status(status).entity(health).build();
    }

    @GET
    @Path("/ready")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readinessCheck() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("timestamp", LocalDateTime.now());
        return Response.ok(readiness).build();
    }

    @GET
    @Path("/live")
    @Produces(MediaType.APPLICATION_JSON)
    public Response livenessCheck() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());
        return Response.ok(liveness).build();
    }
}