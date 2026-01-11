package com.corporate.payroll.adapter.in.web.mapper;

import com.corporate.payroll.adapter.in.web.dto.PaymentDto;
import com.corporate.payroll.domain.model.PayrollPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface PaymentDtoMapper {
    
    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "paymentDate", target = "date")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "status", target = "status")
    PaymentDto toDto(PayrollPayment payment);
    
    default String mapDate(java.time.LocalDate date) {
        return date != null ? date.toString() : null;
    }
}