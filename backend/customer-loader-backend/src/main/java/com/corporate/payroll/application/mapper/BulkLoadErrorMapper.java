package com.corporate.payroll.application.mapper;

import com.corporate.payroll.adapter.in.web.dto.BulkLoadErrorResponseDto;
import com.corporate.payroll.domain.model.BulkLoadError;
import org.mapstruct.Mapper;


@Mapper(componentModel = "cdi")
public interface BulkLoadErrorMapper {
    
    BulkLoadErrorResponseDto toResponseDto(BulkLoadError error);
    
    BulkLoadErrorResponseDto toBulkLoadErrorResponseDto(BulkLoadError error);
}
