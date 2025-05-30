package com.odms.auth.controller;

import com.odms.auth.dto.response.Response;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
