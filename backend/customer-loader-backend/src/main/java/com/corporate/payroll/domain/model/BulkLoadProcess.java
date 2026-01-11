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
public class BulkLoadProcess {
    
    private Long id;
    private String processId;
    private String fileName;
    private String status;
    private Integer totalRecords;
    private Integer successfulCount;
    private Integer errorCount;
    private LocalDateTime processingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
