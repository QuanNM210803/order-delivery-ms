package com.odms.auth.service.impl;

import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.repository.UserRepository;
import com.odms.auth.service.IUserService;
import com.odms.auth.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser() {
        User user = WebUtils.getCurrentUser();
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .build();
    }
}
