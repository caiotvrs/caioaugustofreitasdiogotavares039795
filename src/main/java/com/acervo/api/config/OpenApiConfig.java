package com.acervo.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                                                                .description("Desenvolvimento Local")))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Insira o token JWT (obtido via /auth/login)")));
        }

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("acervo-api")
                                .displayName("Acervo API")
                                .pathsToMatch("/v1/**", "/auth/**")
                                .build();
        }
}
