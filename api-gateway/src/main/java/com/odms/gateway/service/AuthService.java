package com.odms.gateway.service;

import com.odms.gateway.dto.response.Response;
import com.odms.gateway.dto.request.VerifyRequest;
import com.odms.gateway.dto.response.VerifyResponse;
import com.odms.gateway.repository.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthClient authClient;

    public Mono<Response<VerifyResponse>> verifyToken(String token) {
        return authClient.verifyToken(VerifyRequest.builder()
                        .token(token)
                .build());
    }

}
