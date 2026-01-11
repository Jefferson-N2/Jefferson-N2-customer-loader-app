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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para BulkLoadError.
 * Maneja la conversión entre modelos de dominio y entidades JPA.
 * Proporciona métodos de consulta paginados por archivo con resúmenes.
 */
@ApplicationScoped
public class BulkLoadErrorRepositoryAdapter implements BulkLoadErrorRepositoryPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private BulkLoadErrorPersistenceMapper errorMapper;

    @Transactional
    @Override
    public void saveAll(List<BulkLoadError> errors) {
        int batchSize = 1000;
        int i = 0;
        for (BulkLoadError error : errors) {
            BulkLoadErrorEntity entity = errorMapper.toDomainEntity(error);
            entityManager.persist(entity);
            if (++i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }


    @Override
    public List<FileSummaryResponseDto> findAllFilesSummary(long offset, int limit) {
        List<Object[]> results = entityManager.createQuery(
                        "SELECT e.fileName, COUNT(e) " +
                                "FROM BulkLoadErrorEntity e " +
                                "GROUP BY e.fileName " +
                                "ORDER BY e.fileName DESC",
                        Object[].class
                )
                .setFirstResult((int) offset)
                .setMaxResults(limit)
                .getResultList();

        return results.stream()
                .map(row -> {
                    String fileName = (String) row[0];
                    boolean hasErrors = ((Long) row[1]) > 0;
                    return FileSummaryResponseDto.of(fileName, hasErrors);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BulkLoadError> findByFileName(String fileName, long offset, int limit) {
        List<BulkLoadErrorEntity> entities = entityManager.createQuery(
                        "SELECT e FROM BulkLoadErrorEntity e WHERE e.fileName = :fileName " +
                                "ORDER BY e.processingDate DESC, e.rowNumber ASC",
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
}
