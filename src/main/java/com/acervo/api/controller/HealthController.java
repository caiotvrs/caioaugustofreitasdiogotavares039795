package com.acervo.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/actuator")
@RequiredArgsConstructor
@Tag(name = "Observabilidade", description = "Endpoints de saúde e status da aplicação")
public class HealthController {

    private final HealthEndpoint healthEndpoint;
    private final InfoEndpoint infoEndpoint;

    @GetMapping("/health")
    @Operation(summary = "Health Check ", description = "Retorna o status geral da aplicação")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(healthEndpoint.health());
    }

    @GetMapping("/health/liveness")
    @Operation(summary = "Liveness", description = "Verifica se a aplicação está viva")
    public ResponseEntity<?> liveness() {
        return ResponseEntity.ok(healthEndpoint.healthForPath("liveness"));
    }

    @GetMapping("/health/readiness")
    @Operation(summary = "Readiness", description = "Verifica se a aplicação está pronta")
    public ResponseEntity<?> readiness() {
        return ResponseEntity.ok(healthEndpoint.healthForPath("readiness"));
    }

    @GetMapping("/info")
    @Operation(summary = "Informações da Aplicação", description = "Metadados do projeto")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(infoEndpoint.info());
    }
}