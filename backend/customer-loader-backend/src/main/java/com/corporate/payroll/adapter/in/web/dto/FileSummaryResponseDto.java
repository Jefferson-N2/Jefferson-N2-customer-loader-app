package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileSummaryResponseDto implements Serializable {
    
    private String fileName;
    private String status;
    private boolean hasErrors;
    
    public static FileSummaryResponseDto of(String fileName, boolean hasErrors) {
        String status = hasErrors ? "Posee registros con error" : "Procesado correctamente";
        return new FileSummaryResponseDto(fileName, status, hasErrors);
    }
}
