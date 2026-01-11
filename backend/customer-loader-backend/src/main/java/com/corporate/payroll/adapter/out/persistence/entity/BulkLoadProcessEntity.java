package com.corporate.payroll.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_load_processes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLoadProcessEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_id", unique = true, nullable = false, length = 36)
    private String processId;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "total_records")
    private Integer totalRecords;
    
    @Column(name = "successful_count")
    private Integer successfulCount;
    
    @Column(name = "error_count")
    private Integer errorCount;
    
    @Column(name = "processing_date", nullable = false)
    private LocalDateTime processingDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
