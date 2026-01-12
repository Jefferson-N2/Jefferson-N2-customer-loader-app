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

    @Mapping(target = "firstName", source = "client.firstNames")
    @Mapping(target = "lastName", source = "client.lastNames")
    @Mapping(target = "clientCode", source = "client.clientCode")
    @Mapping(target = "idType", source = "client.idType")
    @Mapping(target = "idNumber", source = "client.idNumber")
    @Mapping(target = "email", source = "client.email")
    @Mapping(target = "phoneNumber", source = "client.phoneNumber")
    @Mapping(target = "joinDate", source = "client.joinDate")
    @Mapping(target = "birthDate", source = "client.birthDate")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "firstPayment", source = "lastPayment")
    ClientDetailDto toDto(Client client, Account account, PayrollPayment lastPayment);

    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "balance", source = "payrollValue")
    @Mapping(target = "status", source = "status")
    AccountDetailDto toAccountDto(Account account);

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "status", source = "status")
    FirstPaymentDto toFirstPaymentDto(PayrollPayment payment);
}