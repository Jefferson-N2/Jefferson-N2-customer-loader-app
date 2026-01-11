package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadErrorEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.BulkLoadErrorPersistenceMapper;
import com.corporate.payroll.domain.model.BulkLoadError;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BulkLoadErrorRepositoryAdapter using Mockito.
 */
class BulkLoadErrorRepositoryAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private BulkLoadErrorPersistenceMapper errorMapper;

    @Mock
    private TypedQuery<BulkLoadErrorEntity> queryMock;

    @InjectMocks
    private BulkLoadErrorRepositoryAdapter repositoryAdapter;

    private BulkLoadError errorDomain;
    private BulkLoadErrorEntity errorEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        errorDomain = BulkLoadError.builder()
                .processId("PROC123")
                .clientCode("CLI001")
                .fileName("test.csv")
                .lineNumber(1)
                .errorMessage("Error de prueba")
                .processingDate(LocalDateTime.now())
                .build();

        errorEntity = new BulkLoadErrorEntity();
        errorEntity.setProcessId("PROC123");
        errorEntity.setClientCode("CLI001");
        errorEntity.setFileName("test.csv");
        errorEntity.setLineNumber(1);
        errorEntity.setErrorMessage("Error de prueba");
        errorEntity.setProcessingDate(LocalDateTime.now());
    }

    @Test
    void testSaveAllPersistsEntities() {
        when(errorMapper.toDomainEntity(errorDomain)).thenReturn(errorEntity);

        repositoryAdapter.saveAll(List.of(errorDomain));

        verify(entityManager, times(1)).persist(errorEntity);
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    void testFindByFileNameReturnsMappedList() {
        when(entityManager.createQuery(anyString(), eq(BulkLoadErrorEntity.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("fileName"), anyString())).thenReturn(queryMock);
        when(queryMock.setFirstResult(anyInt())).thenReturn(queryMock);
        when(queryMock.setMaxResults(anyInt())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(errorEntity));
        when(errorMapper.toModel(errorEntity)).thenReturn(errorDomain);

        List<BulkLoadError> result = repositoryAdapter.findByFileName("test.csv", 0, 10);

        assertEquals(1, result.size());
        assertEquals("CLI001", result.getFirst().getClientCode());
    }

    @Test
    void testFindByClientCodeReturnsMappedList() {
        when(entityManager.createQuery(anyString(), eq(BulkLoadErrorEntity.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("clientCode"), anyString())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(errorEntity));
        when(errorMapper.toModel(errorEntity)).thenReturn(errorDomain);

        List<BulkLoadError> result = repositoryAdapter.findByClientCode("CLI001");

        assertEquals(1, result.size());
        assertEquals("PROC123", result.getFirst().getProcessId());
    }

    @Test
    void testFindByClientCodeAndProcessIdReturnsMappedList() {
        when(entityManager.createQuery(anyString(), eq(BulkLoadErrorEntity.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("processId"), anyString())).thenReturn(queryMock);
        when(queryMock.setParameter(eq("clientCode"), anyString())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(errorEntity));
        when(errorMapper.toModel(errorEntity)).thenReturn(errorDomain);

        List<BulkLoadError> result = repositoryAdapter.findByClientCodeAndProcessId("PROC123", "CLI001");

        assertEquals(1, result.size());
        assertEquals("test.csv", result.get(0).getFileName());
    }
}
