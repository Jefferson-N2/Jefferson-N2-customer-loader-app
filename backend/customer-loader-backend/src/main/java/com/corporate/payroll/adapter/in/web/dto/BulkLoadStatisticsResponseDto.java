package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadStatisticsResponseDto {
    
    private int successfulCount;
    private int errorCount;
    private int totalCount;    
    private String message;
    private LocalDateTime processedAt;
}
