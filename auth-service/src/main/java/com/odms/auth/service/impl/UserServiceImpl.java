package com.odms.auth.service.impl;

import com.odms.auth.dto.UserDto;
import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.enums.AuthErrorCode;
import com.odms.auth.repository.RoleRepository;
import com.odms.auth.repository.UserRepository;
import com.odms.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.constant.CommonConstants;
import nmquan.commonlib.dto.response.FilterResponse;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.utils.DateUtils;
import nmquan.commonlib.utils.PageUtils;
import nmquan.commonlib.utils.WebUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse getCurrentUser() {
        User user = userRepository.findById(WebUtils.getCurrentUserId(), false)
                .orElseThrow(() -> new AppException(AuthErrorCode.USERNAME_NOT_EXISTS));
        List<Role> roles = roleRepository.findAllByUserId(user.getId(), false);
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .roles(roles.stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .createdAt(
                        DateUtils.instantToString_HCM(user.getCreatedAt(), CommonConstants.DATE_TIME.DD_MM_YYYY_HH_MM_SS)
                )
                .build();
    }

    @Override
    public Map<Long, UserResponse> getUserByIds(List<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids, false);
        return users.stream()
                .collect(Collectors.toMap(User::getId, user -> UserResponse.builder()
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build()));
    }

    @Override
    public FilterResponse<UserResponse> filterUsers(FilterUserRequest request) {
        List<UserDto> userDTOs = userRepository.filterUsers(request);
        List<UserResponse> userResponses = new ArrayList<>(
                userDTOs.stream()
                .collect(Collectors.groupingBy(
                            UserDto::getId,
                            LinkedHashMap::new,
                            Collectors.collectingAndThen(Collectors.toList(), list -> {
                                UserDto userDto = list.get(0);
                                return UserResponse.builder()
                                        .userId(userDto.getId())
                                        .username(userDto.getUsername())
                                        .fullName(userDto.getFullName())
                                        .email(userDto.getEmail())
                                        .phone(userDto.getPhone())
                                        .address(userDto.getAddress())
                                        .isActive(userDto.getIsActive())
                                        .roles(list.stream()
                                                .map(UserDto::getRoleName)
                                                .toList()
                                        )
                                        .createdAt(DateUtils.instantToString_HCM(
                                                userDto.getCreatedAt(),
                                                CommonConstants.DATE_TIME.DD_MM_YYYY_HH_MM_SS
                                        ))
                                        .build();
                            })
                ))
                .values()
        );
        return PageUtils.manualPagination(userResponses, request.getPageable(), UserResponse.class);
    }
}
