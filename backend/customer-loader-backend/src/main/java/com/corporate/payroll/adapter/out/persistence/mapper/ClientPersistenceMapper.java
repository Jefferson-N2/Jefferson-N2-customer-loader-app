package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {AccountPersistenceMapper.class})
public interface ClientPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientCode", target = "clientCode")
    @Mapping(source = "idType", target = "idType")
    @Mapping(source = "idNumber", target = "idNumber")
    @Mapping(source = "firstNames", target = "firstNames")
    @Mapping(source = "lastNames", target = "lastNames")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "joinDate", target = "joinDate")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(target = "accounts", ignore = true)
    ClientEntity toDomainEntity(Client client);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientCode", target = "clientCode")
    @Mapping(source = "idType", target = "idType")
    @Mapping(source = "idNumber", target = "idNumber")
    @Mapping(source = "firstNames", target = "firstNames")
    @Mapping(source = "lastNames", target = "lastNames")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "joinDate", target = "joinDate")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "accounts", target = "accounts")
    Client toModel(ClientEntity clientEntity);
}
