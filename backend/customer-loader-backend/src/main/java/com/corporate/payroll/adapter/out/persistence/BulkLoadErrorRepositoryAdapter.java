package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.application.port.out.BulkLoadErrorRepositoryPort;
import com.corporate.payroll.adapter.in.web.dto.FileSummaryResponseDto;
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

    @Transactional
    @Override
    public void saveAll(List<BulkLoadError> errors) {
        log.info("Iniciando persistencia de {} errores de carga", errors.size());
        try {
            for (BulkLoadError error : errors) {
                BulkLoadErrorEntity entity = errorMapper.toDomainEntity(error);
                entityManager.persist(entity);
                entityManager.flush();
                entityManager.clear();
            }
            log.info("Persistencia completada: {} errores guardados", errors.size());
        } catch (Exception e) {
            log.error("Error al persistir errores: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BulkLoadError> findByFileName(String fileName, long offset, int limit) {
        List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                        "SELECT e FROM BulkLoadErrorEntity e WHERE e.fileName = :fileName " +
                                "ORDER BY e.processingDate DESC, e.lineNumber ASC",
                        BulkLoadErrorEntity.class
                )
                .setParameter("fileName", fileName)
                .setFirstResult((int) offset)
                .setMaxResults(limit)
                .getResultList();

        return entities.stream()
                .map(errorMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BulkLoadError> findByClientCode(String clientCode) {
        log.debug("Buscando errores para cliente: {}", clientCode);
        try {
            List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                            "SELECT e FROM BulkLoadErrorEntity e WHERE e.clientCode = :clientCode " +
                                    "ORDER BY e.processingDate DESC, e.lineNumber ASC",
                            BulkLoadErrorEntity.class)
                    .setParameter("clientCode", clientCode)
                    .getResultList();

            log.info("Se encontraron {} errores para cliente {}",
                    entities.size(), clientCode);

            return entities.stream()
                    .map(errorMapper::toModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al buscar errores para cliente {}: {}",
                    clientCode, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BulkLoadError> findByClientCodeAndProcessId(String processId, String clientCode) {
        log.debug("Buscando errores para cliente: {} en proceso: {}", clientCode, processId);
        try {
            List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                            "SELECT e FROM BulkLoadErrorEntity e WHERE e.processId = :processId " +
                                    "AND e.clientCode = :clientCode " +
                                    "ORDER BY e.processingDate DESC, e.lineNumber ASC",
                            BulkLoadErrorEntity.class)
                    .setParameter("processId", processId)
                    .setParameter("clientCode", clientCode)
                    .getResultList();

            log.info("Se encontraron {} errores para cliente {} en proceso {}",
                    entities.size(), clientCode, processId);

            return entities.stream()
                    .map(errorMapper::toModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al buscar errores para cliente {} en proceso {}: {}",
                    clientCode, processId, e.getMessage(), e);
            throw e;
        }
    }
}
