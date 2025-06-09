package com.odms.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odms.auth.dto.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterUserRequest extends PageRequest{
    private String username;
    private String fullName;
    private String phone;
    private List<RoleName> roleNames;
}
