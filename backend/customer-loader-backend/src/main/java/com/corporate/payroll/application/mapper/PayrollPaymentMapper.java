package com.corporate.payroll.application.mapper;

import com.corporate.payroll.domain.model.PayrollPayment;
import com.corporate.payroll.adapter.in.web.dto.PayrollPaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {CommonMapperMethods.class})
public interface PayrollPaymentMapper {
    
    @Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "localDateToString")
    @Mapping(source = "amount", target = "amount", qualifiedByName = "bigDecimalToString")
    PayrollPaymentResponseDto toPayrollPaymentResponseDto(PayrollPayment payment);
}
