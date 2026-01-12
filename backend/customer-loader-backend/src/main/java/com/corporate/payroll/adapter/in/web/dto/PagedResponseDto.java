package com.corporate.payroll.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO genérico para respuestas paginadas
 * Compatible con PaginatedResponse del frontend Angular
 * 
 * @param <T> tipo del contenido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponseDto<T> {
    private List<T> content;
    
    private long totalElements;
    
    private int totalPages;
    
    private int size;
    
    private int number;
    
    private boolean empty;
}
