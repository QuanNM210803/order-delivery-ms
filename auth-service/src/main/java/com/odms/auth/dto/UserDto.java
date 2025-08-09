package com.odms.auth.dto;

import nmquan.commonlib.dto.BaseRepositoryDto;

public interface UserDto extends BaseRepositoryDto {
    String getUsername();
    String getFullName();
    String getEmail();
    String getPhone();
    String getAddress();

    Long getRoleId();
    String getRoleName();
    String getRoleDescription();
}
