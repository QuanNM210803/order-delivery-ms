package com.odms.tracking.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

    @Bean
    @Qualifier("internal")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey("X-Internal-Token")) {
                headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            }
            return execution.execute(request, body);
        });

        return restTemplate;
    }

    @Bean
    @Qualifier("external")
    public RestTemplate externalRestTemplate() {
        return new RestTemplate();
    }
}
