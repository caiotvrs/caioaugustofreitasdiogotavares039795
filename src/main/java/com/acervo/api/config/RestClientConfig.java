package com.acervo.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${integration.regionais.base-url}")
    private String baseUrl;

    @Value("${integration.regionais.timeout.connect}")
    private int connectTimeout;

    @Value("${integration.regionais.timeout.read}")
    private int readTimeout;

    @Bean
    public RestClient regionalApiRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            System.out.println("Request URI: " + request.getURI());
            System.out.println("Request Method: " + request.getMethod());
            return execution.execute(request, body);
        };
    }
}
