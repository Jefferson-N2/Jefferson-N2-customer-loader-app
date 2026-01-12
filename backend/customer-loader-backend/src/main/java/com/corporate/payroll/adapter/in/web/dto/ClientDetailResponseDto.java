package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailResponseDto {
    
    private String processId;
    private ClientDetailDto clientDetail;
}

