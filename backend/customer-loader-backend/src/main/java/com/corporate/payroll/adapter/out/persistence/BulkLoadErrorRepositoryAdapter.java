package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
// import com.corporate.payroll.adapter.in.web.dto.FileSummaryResponseDto; // UNUSED - Commented out
import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadErrorEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.BulkLoadErrorPersistenceMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para BulkLoadError.
 * Maneja la conversión entre modelos de dominio y entidades JPA.
 * Proporciona métodos de consulta paginados por archivo con resúmenes.
 */
@Slf4j
@ApplicationScoped
public class BulkLoadErrorRepositoryAdapter implements BulkLoadErrorRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private BulkLoadErrorPersistenceMapper errorMapper;

    @Override
    public void saveAll(List<BulkLoadError> errors) {
        log.info("Iniciando persistencia de {} errores de carga", errors.size());
        for (BulkLoadError error : errors) {
            BulkLoadErrorEntity entity = errorMapper.toDomainEntity(error);
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.clear();
        }
        log.info("Persistencia completada: {} errores guardados", errors.size());
    }

    @Override
    public List<BulkLoadError> findByProcessId(String processId) {
        log.debug("Buscando errores para proceso: {}", processId);
        List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                        "SELECT e FROM BulkLoadErrorEntity e WHERE e.processId = :processId " +
                                "ORDER BY e.lineNumber ASC",
                        BulkLoadErrorEntity.class)
                .setParameter("processId", processId)
                .getResultList();

        log.info("Se encontraron {} errores para proceso {}", entities.size(), processId);

        return entities.stream()
                .map(errorMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BulkLoadError> findByProcessId(String processId, int page, int size) {
        log.debug("Buscando errores para proceso: {} (página: {}, tamaño: {})", processId, page, size);
        List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                        "SELECT e FROM BulkLoadErrorEntity e WHERE e.processId = :processId " +
                                "ORDER BY e.lineNumber ASC",
                        BulkLoadErrorEntity.class)
                .setParameter("processId", processId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        log.info("Se encontraron {} errores para proceso {} (página {})", entities.size(), processId, page);

        return entities.stream()
                .map(errorMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countByProcessId(String processId) {
        log.debug("Contando errores para proceso: {}", processId);
        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(e) FROM BulkLoadErrorEntity e WHERE e.processId = :processId")
                .setParameter("processId", processId)
                .getSingleResult();
        
        log.debug("Total de errores para proceso {}: {}", processId, count);
        return count;
    }
}