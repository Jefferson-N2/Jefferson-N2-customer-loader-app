package com.corporate.payroll.adapter.in.web.service;

import com.corporate.payroll.adapter.in.web.dto.PagedResponseDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PaginationService {

    public <T> PagedResponseDto<T> createPagedResponse(List<T> content, long totalElements, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PagedResponseDto.<T>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .size(size)
                .number(page)
                .empty(content.isEmpty())
                .build();
    }
}