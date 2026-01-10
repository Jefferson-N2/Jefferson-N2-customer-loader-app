package com.corporate.payroll.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadError {
    
    private Long id;
    private String clientCode;
    private String idType;
    private String idNumber;
    private Integer rowNumber;
    private String errorMessage;
    private String errorType;
    private String fileName;
    private LocalDateTime processingDate;
    private LocalDateTime createdAt;
}
