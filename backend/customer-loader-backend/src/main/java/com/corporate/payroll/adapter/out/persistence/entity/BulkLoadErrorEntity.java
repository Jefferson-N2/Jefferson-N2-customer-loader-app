package com.corporate.payroll.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_load_errors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadErrorEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_id", nullable = false, length = 36)
    private String processId;
    
    @Column(name = "client_code")
    private String clientCode;
    
    @Column(name = "id_type", length = 1)
    private String idType;
    
    @Column(name = "id_number", length = 50)
    private String idNumber;
    
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;
    
    @Column(name = "error_message", nullable = false, length = 500)
    private String errorMessage;
    
    @Column(name = "error_type", nullable = false, length = 50)
    private String errorType;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "processing_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime processingDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
