package com.odms.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageInfo {
    private Integer pageIndex;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean hasNextPage;
}
