package com.corporate.payroll.adapter.in.web.metrics;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Interceptor
@PerformanceMetrics
@Slf4j
public class PerformanceMetricsInterceptor {

    @AroundInvoke
    public Object measurePerformance(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getDeclaringClass().getSimpleName() + "." + context.getMethod().getName();
        long startTime = System.currentTimeMillis();
        
        MDC.put("method", methodName);
        MDC.put("startTime", String.valueOf(startTime));
        
        try {
            Object result = context.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            MDC.put("duration", String.valueOf(duration));
            MDC.put("status", "SUCCESS");
            
            if (duration > 5000) {
                log.warn("Método lento detectado: {} tomó {}ms", methodName, duration);
            } else {
                log.info("Método ejecutado: {} en {}ms", methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            MDC.put("status", "ERROR");
            MDC.put("error", e.getClass().getSimpleName());
            
            log.error("Error en método: {} después de {}ms", methodName, duration, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}