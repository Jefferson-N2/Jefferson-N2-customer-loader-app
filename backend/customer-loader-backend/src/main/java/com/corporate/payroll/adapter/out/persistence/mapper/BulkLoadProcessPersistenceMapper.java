package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadProcessEntity;
import com.corporate.payroll.domain.model.BulkLoadProcess;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface BulkLoadProcessPersistenceMapper {
    
    BulkLoadProcessEntity toDomainEntity(BulkLoadProcess bulkLoadProcess);
    
    BulkLoadProcess toModel(BulkLoadProcessEntity entity);
}