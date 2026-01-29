package com.acervo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Acervo Musical API")
                                                .version("1.0.0")
                                                .description("API para gerenciamento de acervo musical")
                                                .contact(new Contact()
                                                                .name("SEPLAG")
                                                                .email("contato@seplag.mt.gov.br")))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080")
                                                                .description("Desenvolvimento Local")));
        }

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("acervo-api")
                                .displayName("Acervo API")
                                .pathsToMatch("/v1/**", "/actuator/**")
                                .build();
        }
}
