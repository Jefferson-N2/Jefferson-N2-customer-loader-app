package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadProcessEntity;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import java.util.Optional;


@RequiredArgsConstructor
@ApplicationScoped
public class BulkLoadProcessRepositoryAdapter implements BulkLoadProcessRepositoryPort {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public BulkLoadProcess save(BulkLoadProcess bulkLoadProcess) {
        BulkLoadProcessEntity entity = BulkLoadProcessEntity.builder()
            .processId(bulkLoadProcess.getProcessId())
            .fileName(bulkLoadProcess.getFileName())
            .status(bulkLoadProcess.getStatus())
            .totalRecords(bulkLoadProcess.getTotalRecords())
            .successfulCount(bulkLoadProcess.getSuccessfulCount())
            .errorCount(bulkLoadProcess.getErrorCount())
            .processingDate(bulkLoadProcess.getProcessingDate())
            .build();
        
        entityManager.persist(entity);
        entityManager.flush();
        
        return toDomain(entity);
    }
    
    @Override
    public Optional<BulkLoadProcess> findByProcessId(String processId) {
        BulkLoadProcessEntity entity = entityManager
            .createQuery("SELECT b FROM BulkLoadProcessEntity b WHERE b.processId = :processId", BulkLoadProcessEntity.class)
            .setParameter("processId", processId)
            .getResultStream()
            .findFirst()
            .orElse(null);
        
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public BulkLoadProcess update(BulkLoadProcess bulkLoadProcess) {
        BulkLoadProcessEntity entity = entityManager.find(BulkLoadProcessEntity.class, bulkLoadProcess.getId());
        
        if (entity != null) {
            entity.setStatus(bulkLoadProcess.getStatus());
            entity.setSuccessfulCount(bulkLoadProcess.getSuccessfulCount());
            entity.setErrorCount(bulkLoadProcess.getErrorCount());
            entity.setTotalRecords(bulkLoadProcess.getTotalRecords());
            
            entityManager.merge(entity);
            entityManager.flush();
        }
        
        return bulkLoadProcess;
    }
    
    private BulkLoadProcess toDomain(BulkLoadProcessEntity entity) {
        return BulkLoadProcess.builder()
            .id(entity.getId())
            .processId(entity.getProcessId())
            .fileName(entity.getFileName())
            .status(entity.getStatus())
            .totalRecords(entity.getTotalRecords())
            .successfulCount(entity.getSuccessfulCount())
            .errorCount(entity.getErrorCount())
            .processingDate(entity.getProcessingDate())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
