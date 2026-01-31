package com.acervo.api.controller;

import com.acervo.api.dto.SyncResultDTO;
import com.acervo.api.service.RegionalSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/regionais")
@RequiredArgsConstructor
@Tag(name = "Regionais", description = "Sincronização de regionais")
public class RegionalController {

    private final RegionalSyncService regionalSyncService;

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar Regionais", description = "Sincroniza dados de regionais com a API. " +
            "Insere novos registros, atualiza modificados (inativa antigo + cria novo), " +
            "e inativa registros removidos da API externa.")
    public ResponseEntity<SyncResultDTO> syncRegionais() {
        SyncResultDTO result = regionalSyncService.syncRegionais();
        return ResponseEntity.ok(result);
    }
}
