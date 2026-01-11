package com.corporate.payroll.application.mapper;

import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.adapter.in.web.dto.ClientResponseDto;
import com.corporate.payroll.adapter.in.web.dto.ClientDetailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = {AccountMapper.class, CommonMapperMethods.class})
public interface ClientMapper {
    
    @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "localDateToString")
    @Mapping(source = "joinDate", target = "joinDate", qualifiedByName = "localDateToString")
    ClientResponseDto toClientResponseDto(Client client);
    
    @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "localDateToString")
    @Mapping(source = "joinDate", target = "joinDate", qualifiedByName = "localDateToString")
    @Mapping(target = "accounts", ignore = true)
    ClientDetailResponseDto toClientDetailResponseDto(Client client);
}
