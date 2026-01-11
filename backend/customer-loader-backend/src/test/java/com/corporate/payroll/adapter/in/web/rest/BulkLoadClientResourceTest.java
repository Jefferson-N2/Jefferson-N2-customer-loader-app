package com.corporate.payroll.adapter.in.web.rest;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadStatisticsResponseDto;
import com.corporate.payroll.application.port.in.BulkLoadClientUseCase;
import com.corporate.payroll.domain.exception.BusinessLogicException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BulkLoadClientResourceTest {

    @Mock
    private BulkLoadClientUseCase bulkLoadUseCase;

    @InjectMocks
    private BulkLoadClientResource bulkLoadClientResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadClientsWithValidFileReturnsSuccessResponse() {
        String fileName = "test_clients.txt";
        String fileContent = "C|12345678|2024-01-15|50000.00|test@email.com|3125551234";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        
        BulkLoadStatisticsResponseDto mockStats = BulkLoadStatisticsResponseDto.builder()
                .processId("test-process-id")
                .successfulCount(1)
                .errorCount(0)
                .totalCount(1)
                .message("Procesamiento completado")
                .processedAt(LocalDateTime.now())
                .build();

        when(bulkLoadUseCase.processBulkLoad(any(InputStream.class), eq(fileName)))
                .thenReturn(mockStats);

        Response response = bulkLoadClientResource.uploadClients(inputStream, fileName);

        assertEquals(200, response.getStatus(), "Should return HTTP 200 for successful upload");
        verify(bulkLoadUseCase).processBulkLoad(inputStream, fileName);
    }

    @Test
    void testUploadClientsWithNullInputStreamThrowsException() {
        String fileName = "test_clients.txt";
        
        when(bulkLoadUseCase.processBulkLoad(null, fileName))
                .thenThrow(new BusinessLogicException("El archivo es requerido"));

        assertThrows(BusinessLogicException.class, () -> {
            bulkLoadClientResource.uploadClients(null, fileName);
        }, "Should throw exception for null input stream");
    }

    @Test
    void testUploadClientsWithNullFileNameThrowsException() {
        String fileContent = "C|12345678|2024-01-15|50000.00|test@email.com|3125551234";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        
        when(bulkLoadUseCase.processBulkLoad(any(InputStream.class), eq(null)))
                .thenThrow(new BusinessLogicException("El archivo no tiene nombre"));

        assertThrows(BusinessLogicException.class, () -> {
            bulkLoadClientResource.uploadClients(inputStream, null);
        }, "Should throw exception for null file name");
    }

    @Test
    void testUploadClientsWithEmptyFileNameThrowsException() {
        String fileContent = "C|12345678|2024-01-15|50000.00|test@email.com|3125551234";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        String fileName = "";
        
        when(bulkLoadUseCase.processBulkLoad(any(InputStream.class), eq(fileName)))
                .thenThrow(new BusinessLogicException("El archivo no tiene nombre"));

        assertThrows(BusinessLogicException.class, () -> {
            bulkLoadClientResource.uploadClients(inputStream, fileName);
        }, "Should throw exception for empty file name");
    }

    @Test
    void testUploadClientsWithProcessingErrorsReturnsResponseWithErrors() {
        String fileName = "test_clients_with_errors.txt";
        String fileContent = "X|invalid|invalid-date|invalid-amount|invalid-email|123";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        
        BulkLoadStatisticsResponseDto mockStats = BulkLoadStatisticsResponseDto.builder()
                .processId("test-process-id")
                .successfulCount(0)
                .errorCount(1)
                .totalCount(1)
                .message("Procesamiento completado con errores")
                .processedAt(LocalDateTime.now())
                .build();

        when(bulkLoadUseCase.processBulkLoad(any(InputStream.class), eq(fileName)))
                .thenReturn(mockStats);

        Response response = bulkLoadClientResource.uploadClients(inputStream, fileName);

        assertEquals(200, response.getStatus(), "Should return HTTP 200 even with processing errors");
        verify(bulkLoadUseCase).processBulkLoad(inputStream, fileName);
    }
}