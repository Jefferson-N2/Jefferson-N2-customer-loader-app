package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.ClientPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientRepositoryAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ClientPersistenceMapper clientMapper;

    @InjectMocks
    private ClientRepositoryAdapter clientRepositoryAdapter;

    private Client testClient;
    private ClientEntity testClientEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Preparar datos de prueba
        testClient = Client.builder()
                .id(1L)
                .clientCode("CLI_ABC123")
                .idType("C")
                .idNumber("1234567890")
                .firstNames("Juan Carlos")
                .lastNames("García López")
                .birthDate(LocalDate.of(1985, 3, 15))
                .joinDate(LocalDate.of(2024, 1, 10))
                .email("juan@example.com")
                .phoneNumber("3101234567")
                .processId("process-123")
                .build();

        testClientEntity = ClientEntity.builder()
                .id(1L)
                .clientCode("CLI_ABC123")
                .idType("C")
                .idNumber("1234567890")
                .firstNames("Juan Carlos")
                .lastNames("García López")
                .birthDate(LocalDate.of(1985, 3, 15))
                .joinDate(LocalDate.of(2024, 1, 10))
                .email("juan@example.com")
                .phoneNumber("3101234567")
                .processId("process-123")
                .build();
    }

    @Test
    void testFindByProcessIdShouldReturnPaginatedClients() {
        String processId = "process-123";
        int page = 0;
        int size = 20;

        TypedQuery<ClientEntity> query = mock(TypedQuery.class);
        List<ClientEntity> entityList = new ArrayList<>();
        entityList.add(testClientEntity);

        when(entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.processId = :processId ORDER BY c.id DESC",
                ClientEntity.class))
                .thenReturn(query);
        when(query.setParameter("processId", processId)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(20)).thenReturn(query);
        when(query.getResultList()).thenReturn(entityList);
        when(clientMapper.toModel(testClientEntity)).thenReturn(testClient);

        List<Client> result = clientRepositoryAdapter.findByProcessId(processId, page, size);

        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CLI_ABC123", result.get(0).getClientCode());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(20);
    }

    @Test
    void testCountByProcessIdShouldReturnCorrectCount() {
        String processId = "process-123";
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT COUNT(c) FROM ClientEntity c WHERE c.processId = :processId",
                Long.class))
                .thenReturn(query);
        when(query.setParameter("processId", processId)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L);

        long result = clientRepositoryAdapter.countByProcessId(processId);

        
        assertEquals(5L, result);
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("findAllPaginated() debe retornar lista paginada de todos los clientes")
    void testFindAllPaginatedShouldReturnAllClientsWithPagination() {
        int page = 0;
        int size = 10;

        TypedQuery<ClientEntity> query = mock(TypedQuery.class);
        List<ClientEntity> entityList = new ArrayList<>();
        entityList.add(testClientEntity);

        when(entityManager.createQuery(
                "SELECT c FROM ClientEntity c ORDER BY c.id DESC",
                ClientEntity.class))
                .thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(entityList);
        when(clientMapper.toModel(testClientEntity)).thenReturn(testClient);

        List<Client> result = clientRepositoryAdapter.findAllPaginated(page, size);

        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void testExistsByIdNumberWithExistingClientShouldReturnTrue() {
        String idNumber = "1234567890";
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT COUNT(c) FROM ClientEntity c WHERE c.idNumber = :idNumber",
                Long.class))
                .thenReturn(query);
        when(query.setParameter("idNumber", idNumber)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean result = clientRepositoryAdapter.existsByIdNumber(idNumber);

        
        assertTrue(result);
    }

    @Test
    void testExistsByIdNumberWithoutExistingClientShouldReturnFalse() {
        String idNumber = "9999999999";
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT COUNT(c) FROM ClientEntity c WHERE c.idNumber = :idNumber",
                Long.class))
                .thenReturn(query);
        when(query.setParameter("idNumber", idNumber)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean result = clientRepositoryAdapter.existsByIdNumber(idNumber);

        
        assertFalse(result);
    }

    @Test
    void testFindByProcessIdWithMultiplePagesShouldPaginateCorrectly() {
        String processId = "process-123";
        int page = 2;
        int size = 10;
        int expectedOffset = page * size; // 20

        TypedQuery<ClientEntity> query = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.processId = :processId ORDER BY c.id DESC",
                ClientEntity.class))
                .thenReturn(query);
        when(query.setParameter("processId", processId)).thenReturn(query);
        when(query.setFirstResult(expectedOffset)).thenReturn(query);
        when(query.setMaxResults(size)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        clientRepositoryAdapter.findByProcessId(processId, page, size);

        
        verify(query).setFirstResult(expectedOffset);
        verify(query).setMaxResults(size);
    }
}
