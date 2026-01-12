package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import com.corporate.payroll.adapter.out.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi")
public interface AccountPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "payrollValue", target = "payrollValue")
    @Mapping(source = "status", target = "status")
    AccountEntity toDomainEntity(Account account);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "payrollValue", target = "payrollValue")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "payments", ignore = true)
    Account toModel(AccountEntity accountEntity);
}
