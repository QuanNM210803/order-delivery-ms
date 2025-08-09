package com.odms.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.VerifyResponse;
import nmquan.commonlib.dto.response.IDResponse;

public interface IAuthService {
    LoginResponse loginAccount(LoginRequest loginRequest);
    VerifyResponse verifyToken(VerifyRequest verifyRequest);
    IDResponse<Long> registerAccount(RegisterRequest request, String roleName) throws JsonProcessingException;
    void verifyEmail(String token);
}
