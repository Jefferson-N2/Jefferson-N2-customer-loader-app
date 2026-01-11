package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.PayrollPayment;
import com.corporate.payroll.adapter.out.persistence.entity.PayrollPaymentEntity;
import com.corporate.payroll.adapter.out.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi")
public interface PayrollPaymentPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountId", target = "account", qualifiedByName = "accountIdToEntity")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    PayrollPaymentEntity toDomainEntity(PayrollPayment payrollPayment);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "account", target = "accountId", qualifiedByName = "accountToId")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    PayrollPayment toModel(PayrollPaymentEntity payrollPaymentEntity);
    
    @Named("accountIdToEntity")
    default AccountEntity accountIdToEntity(Long accountId) {
        if (accountId == null) {
            return null;
        }
        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        return account;
    }
    
    @Named("accountToId")
    default Long accountToId(AccountEntity account) {
        return account != null ? account.getId() : null;
    }
}
