package com.odms.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odms.auth.constant.Message;
import com.odms.auth.enums.RoleName;
import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.VerifyResponse;
import com.odms.auth.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.LocalizationUtils;
import nmquan.commonlib.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> loginAccount(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginAccount(loginRequest);
        return ResponseUtils.success(loginResponse, localizationUtils.getLocalizedMessage(Message.LOGIN_SUCCESS));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Response<VerifyResponse>> verifyToken(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse verifyResponse = authService.verifyToken(verifyRequest);
        return ResponseUtils.success(verifyResponse);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<Response<IDResponse<Long>>> registerAccountCustomer(@Valid @RequestBody RegisterRequest request) throws JsonProcessingException {
        String roleName = "CUSTOMER";
        IDResponse<Long> response = authService.registerAccount(request, roleName);
        return ResponseUtils.success(response, localizationUtils.getLocalizedMessage(Message.REGISTER_SUCCESS));
    }

    @PostMapping("/register/system-user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<IDResponse<Long>>> registerAccountDeliveryStaff(@Valid @RequestBody RegisterRequest request) throws JsonProcessingException {
        String roleName = null;
        if(RoleName.DELIVERY_STAFF.equals(request.getRoleName())){
            roleName = "DELIVERY_STAFF";
        } else if (RoleName.ADMIN.equals(request.getRoleName())) {
            roleName = "ADMIN";
        }
        IDResponse<Long> response = authService.registerAccount(request, roleName);
        return ResponseUtils.success(response, localizationUtils.getLocalizedMessage(Message.REGISTER_SUCCESS));
    }

    @PatchMapping("/verify-email/{token}")
    public ResponseEntity<Response<Boolean>> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseUtils.success(true, localizationUtils.getLocalizedMessage(Message.VERIFY_EMAIL_SUCCESS));
    }
}
