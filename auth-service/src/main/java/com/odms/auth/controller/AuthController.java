package com.odms.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.IDResponse;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.Response;
import com.odms.auth.dto.response.VerifyResponse;
import com.odms.auth.service.IAuthService;
import com.odms.auth.utils.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> loginAccount(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginAccount(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<LoginResponse>builder()
                .data(loginResponse)
                .message(Message.LOGIN_SUCCESS.getMessage())
                .build());
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Response<VerifyResponse>> verifyToken(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse verifyResponse = authService.verifyToken(verifyRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<VerifyResponse>builder()
                .data(verifyResponse)
                .build());
    }

    @PostMapping("/register/customer")
    public ResponseEntity<Response<IDResponse<Integer>>> registerAccountCustomer(@Valid @RequestBody RegisterRequest request) throws JsonProcessingException {
        String roleName = "CUSTOMER";
        IDResponse<Integer> response = authService.registerAccount(request, roleName);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<IDResponse<Integer>>builder()
                .data(response)
                .message(Message.REGISTER_SUCCESS.getMessage())
                .build());
    }

    @PostMapping("/register/deliverystaff")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<IDResponse<Integer>>> registerAccountDeliveryStaff(@Valid @RequestBody RegisterRequest request) throws JsonProcessingException {
        String roleName = "DELIVERY_STAFF";
        IDResponse<Integer> response = authService.registerAccount(request, roleName);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<IDResponse<Integer>>builder()
                .data(response)
                .message(Message.REGISTER_SUCCESS.getMessage())
                .build());
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<IDResponse<Integer>>> registerAccountAdmin(@Valid @RequestBody RegisterRequest request) throws JsonProcessingException {
        String roleName = "ADMIN";
        IDResponse<Integer> response = authService.registerAccount(request, roleName);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<IDResponse<Integer>>builder()
                .data(response)
                .message(Message.REGISTER_SUCCESS.getMessage())
                .build());
    }

    @PatchMapping("/verify-email/{token}")
    public ResponseEntity<Response<Boolean>> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(true)
                .message(Message.VERIFY_EMAIL_SUCCESS.getMessage())
                .build());
    }
}
