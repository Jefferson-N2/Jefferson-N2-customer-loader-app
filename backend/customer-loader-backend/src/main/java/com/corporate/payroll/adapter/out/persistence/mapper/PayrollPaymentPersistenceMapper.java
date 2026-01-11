package com.corporate.payroll.adapter.out.persistence.mapper;

import com.corporate.payroll.domain.model.PayrollPayment;
import com.corporate.payroll.adapter.out.persistence.entity.PayrollPaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface PayrollPaymentPersistenceMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    PayrollPaymentEntity toDomainEntity(PayrollPayment payrollPayment);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountId", target = "accountId")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    PayrollPayment toModel(PayrollPaymentEntity payrollPaymentEntity);
}
