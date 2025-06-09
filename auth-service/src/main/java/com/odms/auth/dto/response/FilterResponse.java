package com.odms.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.auth.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterResponse<T>{
    @Builder.Default
    private List<T> data = Collections.emptyList();

    private PageInfo pageInfo;
}
