package com.corporate.payroll.application.service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RowProcessingContext {
    private String idType;
    private String idNumber;
    private String joinDate;
    private String payrollValue;
    private String email;
    private String phoneNumber;
    private Integer lineNumber;
    private String fileName;
    private LocalDateTime processingDate;
    private String processId;
}