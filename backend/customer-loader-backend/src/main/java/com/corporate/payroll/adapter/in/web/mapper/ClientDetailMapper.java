package com.corporate.payroll.adapter.in.web.mapper;

import com.corporate.payroll.adapter.in.web.dto.AccountDetailDto;
import com.corporate.payroll.adapter.in.web.dto.ClientDetailDto;
import com.corporate.payroll.adapter.in.web.dto.FirstPaymentDto;
import com.corporate.payroll.domain.model.Account;
import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.PayrollPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ClientDetailMapper {

    @Mapping(target = "firstName", source = "firstNames")
    @Mapping(target = "lastName", source = "lastNames")
    @Mapping(target = "account", source = "firstAccount")
    @Mapping(target = "firstPayment", source = "firstPayment")
    ClientDetailDto toDto(Client client);

    @Mapping(target = "firstName", source = "client.firstNames")
    @Mapping(target = "lastName", source = "client.lastNames")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "firstPayment", source = "payment")
    ClientDetailDto toDto(Client client, Account account, PayrollPayment payment);

    @Mapping(target = "balance", source = "payrollValue")
    AccountDetailDto toAccountDto(Account account);

    FirstPaymentDto toFirstPaymentDto(PayrollPayment payment);
}