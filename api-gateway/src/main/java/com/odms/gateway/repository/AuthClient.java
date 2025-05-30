package com.odms.gateway.repository;


import com.odms.gateway.dto.response.Response;
import com.odms.gateway.dto.request.VerifyRequest;
import com.odms.gateway.dto.response.VerifyResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface AuthClient {

    @PostExchange(url = "/auth/auth/verify-token", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<Response<VerifyResponse>> verifyToken(@RequestBody VerifyRequest verifyRequest);

}
