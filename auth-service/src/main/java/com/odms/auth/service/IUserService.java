package com.odms.auth.service;

import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.entity.User;

public interface IUserService {
    UserResponse getCurrentUser();
}
