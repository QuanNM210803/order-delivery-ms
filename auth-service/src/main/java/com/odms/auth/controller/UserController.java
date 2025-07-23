package com.odms.auth.controller;

import com.odms.auth.annotation.InternalApi;
import com.odms.auth.dto.request.FilterUserRequest;
import com.odms.auth.dto.request.internal.IdListRequest;
import com.odms.auth.dto.response.FilterResponse;
import com.odms.auth.dto.response.Response;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<UserResponse>builder()
                        .data(user)
                        .build()
        );
    }

    @PostMapping("/internal/info/users")
    @InternalApi
    public ResponseEntity<Response<Map<Integer, UserResponse>>> getUserByIds(@RequestBody IdListRequest ids) {
        Map<Integer, UserResponse> user = userService.getUserByIds(ids.getIds());
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Map<Integer, UserResponse>>builder()
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FilterResponse<UserResponse>>> filterUsers(@Valid @ModelAttribute FilterUserRequest request) {
        FilterResponse<UserResponse> response = userService.filterUsers(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<FilterResponse<UserResponse>>builder()
                        .data(response)
                        .build()
        );
    }
}
