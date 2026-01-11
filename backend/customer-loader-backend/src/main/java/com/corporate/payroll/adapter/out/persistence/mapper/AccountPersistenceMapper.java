package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {PayrollPaymentPersistenceMapper.class})
public interface AccountPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "clientId", target = "client.id")
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
}
