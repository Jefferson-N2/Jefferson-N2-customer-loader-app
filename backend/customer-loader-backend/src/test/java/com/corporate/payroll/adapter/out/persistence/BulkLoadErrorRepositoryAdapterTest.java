package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadErrorEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.BulkLoadErrorPersistenceMapper;
import com.corporate.payroll.domain.model.BulkLoadError;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
                .lineNumber(1)
                .errorMessage("Error de prueba")
                .build();

        errorEntity = new BulkLoadErrorEntity();
        errorEntity.setProcessId("PROC123");
        errorEntity.setLineNumber(1);
        errorEntity.setErrorMessage("Error de prueba");
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
    void testFindByProcessIdReturnsMappedList() {
        when(entityManager.createQuery(anyString(), eq(BulkLoadErrorEntity.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("processId"), anyString())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(errorEntity));
        when(errorMapper.toModel(errorEntity)).thenReturn(errorDomain);

        List<BulkLoadError> result = repositoryAdapter.findByProcessId("PROC123");

        assertEquals(1, result.size());
    }

    @Test
    void testFindByProcessIdWithPaginationReturnsMappedList() {
        when(entityManager.createQuery(anyString(), eq(BulkLoadErrorEntity.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("processId"), anyString())).thenReturn(queryMock);
        when(queryMock.setFirstResult(anyInt())).thenReturn(queryMock);
        when(queryMock.setMaxResults(anyInt())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(List.of(errorEntity));
        when(errorMapper.toModel(errorEntity)).thenReturn(errorDomain);

        List<BulkLoadError> result = repositoryAdapter.findByProcessId("PROC123", 0, 10);

        assertEquals(1, result.size());
    }
}
