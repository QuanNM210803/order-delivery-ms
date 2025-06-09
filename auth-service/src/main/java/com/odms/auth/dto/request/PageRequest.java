package com.odms.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageRequest {
    @Min(value = 1, message = "Chỉ số trang phải lớn hơn 0")
    private Integer pageIndex;

    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    private Integer pageSize;

    private String sortField;
    private String sortDirection;
}
