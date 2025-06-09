package com.odms.auth.service.impl;

import com.odms.auth.dto.PageInfo;
import com.odms.auth.dto.RoleName;
import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.response.FilterResponse;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.repository.UserRepository;
import com.odms.auth.service.IUserService;
import com.odms.auth.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
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
                .createdAt(user.getCreatedAt().format(formatter))
                .build();
    }

    @Override
    public Map<Integer, UserResponse> getUserByIds(List<Integer> ids) {
        List<User> users = userRepository.findAllByUserIdIn(ids);
        return users.stream()
                .collect(Collectors.toMap(User::getUserId, user -> UserResponse.builder()
                        .userId(user.getUserId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build()));
    }

    @Override
    public FilterResponse<UserResponse> filterUsers(FilterUserRequest request) {
        int pageIndex = request.getPageIndex() != null ? request.getPageIndex() - 1 : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        String username = StringUtils.isNoneBlank(request.getUsername()) ? request.getUsername().trim() : null;
        String fullName = StringUtils.isNoneBlank(request.getFullName()) ? request.getFullName().trim().toLowerCase() : null;
        String phone = StringUtils.isNoneBlank(request.getPhone()) ? request.getPhone().trim() : null;
        List<String> roleNames = request.getRoleNames() != null ? request.getRoleNames()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList()) : null;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<User> userPage = userRepository.filterUsers(
                username,
                fullName,
                phone,
                roleNames,
                pageable
        );
        PageInfo pageInfo = PageInfo.builder()
                .pageIndex(userPage.getNumber() + 1)
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .hasNextPage(userPage.hasNext())
                .build();
        List<User> users = userPage.getContent();
        List<UserResponse> userResponses = users.stream()
                .map(user -> UserResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .roles(user.getRoles().stream()
                                .map(Role::getName)
                                .toList())
                        .build())
                .toList();
        return FilterResponse.<UserResponse>builder()
                .data(userResponses)
                .pageInfo(pageInfo)
                .build();
    }
}
