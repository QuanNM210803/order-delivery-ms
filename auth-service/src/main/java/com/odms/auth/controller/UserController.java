package com.odms.auth.controller;

import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.request.internal.IdListRequest;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.annotation.InternalRequest;
import nmquan.commonlib.dto.response.FilterResponse;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/my-info")
    public ResponseEntity<Response<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseUtils.success(user);
    }

    @PostMapping("/internal/info/users")
    @InternalRequest
    public ResponseEntity<Response<Map<Long, UserResponse>>> getUserByIds(@RequestBody IdListRequest ids) {
        Map<Long, UserResponse> user = userService.getUserByIds(ids.getIds());
        return ResponseUtils.success(user);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FilterResponse<UserResponse>>> filterUsers(@Valid @ModelAttribute FilterUserRequest request) {
        FilterResponse<UserResponse> response = userService.filterUsers(request);
        return ResponseUtils.success(response);
    }
}
