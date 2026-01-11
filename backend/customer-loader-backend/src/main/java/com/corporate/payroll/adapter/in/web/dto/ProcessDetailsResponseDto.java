package com.corporate.payroll.adapter.in.web.dto;

import com.corporate.payroll.domain.model.Client;
import com.corporate.payroll.domain.model.BulkLoadError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessDetailsResponseDto {
    
    private String processId;
    private String fileName;
    private String status;
    private Integer totalRecords;
    private Integer successfulCount;
    private Integer errorCount;
    private LocalDateTime processingDate;
    private List<Client> clients;
    private List<BulkLoadError> errors;
}