package com.corporate.payroll.adapter.in.web.cors;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@WebFilter("/*")
public class CorsFilter implements Filter {

    private List<String> allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private boolean allowCredentials;
    private String maxAge;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String origins = System.getenv("CORS_ALLOWED_ORIGINS");
        allowedOrigins = Objects.isNull(origins) ? Arrays.asList(origins.split(",")) : 
                        Arrays.asList("http://localhost:4200");
        
        allowedMethods = System.getenv("CORS_ALLOWED_METHODS");
        if (allowedMethods == null) {
            allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        }
        
        allowedHeaders = System.getenv("CORS_ALLOWED_HEADERS");
        if (allowedHeaders == null) {
            allowedHeaders = "Content-Type,Authorization,X-Requested-With";
        }
        
        String credentials = System.getenv("CORS_ALLOW_CREDENTIALS");
        allowCredentials = credentials != null ? Boolean.parseBoolean(credentials) : true;
        
        maxAge = System.getenv("CORS_MAX_AGE");
        if (maxAge == null) {
            maxAge = "3600";
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String origin = httpRequest.getHeader("Origin");
        
        if (origin != null && allowedOrigins.contains(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        httpResponse.setHeader("Access-Control-Allow-Methods", allowedMethods);
        httpResponse.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        httpResponse.setHeader("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
        httpResponse.setHeader("Access-Control-Max-Age", maxAge);
        
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}