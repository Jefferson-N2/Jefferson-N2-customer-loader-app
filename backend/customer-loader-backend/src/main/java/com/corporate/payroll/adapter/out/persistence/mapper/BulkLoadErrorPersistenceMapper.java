package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.BulkLoadError;
import com.corporate.payroll.adapter.out.persistence.entity.BulkLoadErrorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface BulkLoadErrorPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "processId", target = "processId")
    @Mapping(source = "lineNumber", target = "lineNumber")
    @Mapping(source = "fieldName", target = "fieldName")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "createdAt", target = "createdAt")
    BulkLoadErrorEntity toDomainEntity(BulkLoadError bulkLoadError);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "processId", target = "processId")
    @Mapping(source = "lineNumber", target = "lineNumber")
    @Mapping(source = "fieldName", target = "fieldName")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "createdAt", target = "createdAt")
    BulkLoadError toModel(BulkLoadErrorEntity bulkLoadErrorEntity);
}
