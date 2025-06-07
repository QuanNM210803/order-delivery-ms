package com.odms.auth.controller;

import com.odms.auth.dto.request.internal.IdListRequest;
import com.odms.auth.dto.response.Response;
import com.odms.auth.dto.response.UserResponse;
import com.odms.auth.exception.AppException;
import com.odms.auth.exception.ErrorCode;
import com.odms.auth.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

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
    public ResponseEntity<Response<Map<Integer, UserResponse>>> getUserByIds(@RequestBody IdListRequest ids, HttpServletRequest request) {
        String x_internal_token = request.getHeader("X-Internal-Token");
        if (!X_INTERNAL_TOKEN.equals(x_internal_token)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Map<Integer, UserResponse> user = userService.getUserByIds(ids.getIds());
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Map<Integer, UserResponse>>builder()
                        .data(user)
                        .build()
        );
    }
}
