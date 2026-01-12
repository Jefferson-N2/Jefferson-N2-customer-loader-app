package com.corporate.payroll.adapter.out.persistence;

import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadProcessEntity;
import com.corporate.payroll.adapter.out.persistence.mapper.BulkLoadProcessPersistenceMapper;
import com.corporate.payroll.application.port.out.BulkLoadProcessRepositoryPort;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@ApplicationScoped
public class BulkLoadProcessRepositoryAdapter implements BulkLoadProcessRepositoryPort {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    private BulkLoadProcessPersistenceMapper mapper;
    
    @Override
    public BulkLoadProcess save(BulkLoadProcess bulkLoadProcess) {
        BulkLoadProcessEntity entity = mapper.toDomainEntity(bulkLoadProcess);
        entityManager.persist(entity);
        entityManager.flush();
        return mapper.toModel(entity);
    }
    
    @Override
    public Optional<BulkLoadProcess> findByProcessId(String processId) {
        BulkLoadProcessEntity entity = entityManager
            .createQuery("SELECT b FROM BulkLoadProcessEntity b WHERE b.processId = :processId", BulkLoadProcessEntity.class)
            .setParameter("processId", processId)
            .getResultStream()
            .findFirst()
            .orElse(null);
        
        return entity != null ? Optional.of(mapper.toModel(entity)) : Optional.empty();
    }
    
    @Override
    public BulkLoadProcess update(BulkLoadProcess bulkLoadProcess) {
        Optional<BulkLoadProcess> existing = findByProcessId(bulkLoadProcess.getProcessId());
        
        if (existing.isPresent()) {
            bulkLoadProcess.setId(existing.get().getId());
        }
        
        BulkLoadProcessEntity entity = mapper.toDomainEntity(bulkLoadProcess);
        entity = entityManager.merge(entity);
        entityManager.flush();
        return mapper.toModel(entity);
    }
    
    @Override
    public List<BulkLoadProcess> findAll(int page, int size) {
        List<BulkLoadProcessEntity> entities = entityManager
            .createQuery("SELECT b FROM BulkLoadProcessEntity b ORDER BY b.processingDate DESC", BulkLoadProcessEntity.class)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
        
        return entities.stream()
            .map(mapper::toModel)
            .collect(Collectors.toList());
    }
    
    @Override
    public long countAll() {
        return entityManager
            .createQuery("SELECT COUNT(b) FROM BulkLoadProcessEntity b", Long.class)
            .getSingleResult();
    }
}
