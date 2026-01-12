package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ClientPersistenceMapper {
    
    ClientEntity toDomainEntity(Client client);
    
    @Mapping(target = "accounts", ignore = true)
    Client toModel(ClientEntity clientEntity);
}
