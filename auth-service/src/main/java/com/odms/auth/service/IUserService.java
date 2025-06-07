package com.odms.auth.service;

import com.odms.auth.dto.response.UserResponse;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserResponse getCurrentUser();
    Map<Integer, UserResponse> getUserByIds(List<Integer> ids);
}
