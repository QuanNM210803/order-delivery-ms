package com.odms.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nmquan.commonlib.dto.request.FilterRequest;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterUserRequest extends FilterRequest {
    private String username;
    private String fullName;
    private String phone;
    private Boolean active;
    private List<Long> roleIds;
}
