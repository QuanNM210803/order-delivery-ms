package com.odms.auth.service;

import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.response.FilterResponse;
import com.odms.auth.dto.response.UserResponse;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserResponse getCurrentUser();
    Map<Integer, UserResponse> getUserByIds(List<Integer> ids);
    FilterResponse<UserResponse> filterUsers(FilterUserRequest request);
}
