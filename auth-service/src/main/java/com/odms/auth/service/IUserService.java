package com.odms.auth.service;

import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.response.UserResponse;
import nmquan.commonlib.dto.response.FilterResponse;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserResponse getCurrentUser();
    Map<Long, UserResponse> getUserByIds(List<Long> ids);
    FilterResponse<UserResponse> filterUsers(FilterUserRequest request);
}
