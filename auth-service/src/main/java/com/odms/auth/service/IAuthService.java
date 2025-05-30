package com.odms.auth.service;

import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.IDResponse;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.VerifyResponse;

public interface IAuthService {
    LoginResponse loginAccount(LoginRequest loginRequest);
    VerifyResponse verifyToken(VerifyRequest verifyRequest);
    IDResponse<Integer> registerAccount(RegisterRequest request, String roleName);
}
