package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Data
@Builder
public class PageInfo {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public static PageInfo of(Page<?> page) {
        return PageInfo.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getPageable().getPageNumber())
                .pageSize(page.getPageable().getPageSize())
                .build();
    }

    public static PageInfo of( Long totalElements, Integer totalPages, Integer currentPage, Integer pageSize ) {
        return PageInfo.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
}
