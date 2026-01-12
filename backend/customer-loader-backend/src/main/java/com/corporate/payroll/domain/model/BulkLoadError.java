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
    private String processId;
    private Integer lineNumber;
    private String errorMessage;
    private String fieldName;
    private LocalDateTime createdAt;
}
