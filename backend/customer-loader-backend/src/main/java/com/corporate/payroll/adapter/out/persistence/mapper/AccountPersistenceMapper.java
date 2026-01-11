package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi", uses = {PayrollPaymentPersistenceMapper.class})
public interface AccountPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "clientId", target = "client", qualifiedByName = "clientIdToEntity")
    @Mapping(source = "payrollValue", target = "payrollValue")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "payments", ignore = true)
    AccountEntity toDomainEntity(Account account);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "payrollValue", target = "payrollValue")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "payments", target = "payments")
    Account toModel(AccountEntity accountEntity);
    
    @Named("clientIdToEntity")
    default ClientEntity clientIdToEntity(Long clientId) {
        if (clientId == null) {
            return null;
        }
        ClientEntity client = new ClientEntity();
        client.setId(clientId);
        return client;
    }
}
