package com.corporate.payroll.application.mapper;

import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.adapter.in.web.dto.AccountResponseDto;
import com.corporate.payroll.adapter.in.web.dto.AccountDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {PayrollPaymentMapper.class, CommonMapperMethods.class})
public interface AccountMapper {
    
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "payrollValue", target = "payrollValue", qualifiedByName = "bigDecimalToString")
    AccountResponseDto toAccountResponseDto(Account account);
    
    @Mapping(source = "payrollValue", target = "payrollValue", qualifiedByName = "bigDecimalToString")
    @Mapping(target = "payments", ignore = true)
    AccountDetailDto toAccountDetailDto(Account account);
}
