package com.corporate.payroll.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_load_errors", indexes = {
    @Index(name = "idx_client_code", columnList = "client_code"),
    @Index(name = "idx_id_number", columnList = "id_number"),
    @Index(name = "idx_file_name", columnList = "file_name"),
    @Index(name = "idx_processing_date", columnList = "processing_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadErrorEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_code")
    private String clientCode;
    
    @Column(name = "id_type", length = 1)
    private String idType;
    
    @Column(name = "id_number", length = 50)
    private String idNumber;
    
    @Column(name = "row_index", nullable = false)
    private Integer rowNumber;
    
    @Column(name = "error_message", nullable = false, length = 500)
    private String errorMessage;
    
    @Column(name = "error_type", nullable = false, length = 50)
    private String errorType;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "processing_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDateTime processingDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
