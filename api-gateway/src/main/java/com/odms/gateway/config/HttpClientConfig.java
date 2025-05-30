package com.odms.gateway.config;

import com.odms.gateway.repository.AuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {
    @Value("${server.host_auth_service}")
    private String hostAuth;

    @Value("${server.port_auth_service}")
    private String portAuth;

    @Bean
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl("http://" + hostAuth + ":" + portAuth)
                .build();
    }
    @Bean
    AuthClient authClient(WebClient authWebClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(authWebClient))
                .build();

        return factory.createClient(AuthClient.class);
    }
}
