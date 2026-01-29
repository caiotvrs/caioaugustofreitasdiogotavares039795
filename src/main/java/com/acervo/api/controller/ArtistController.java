package com.acervo.api.controller;

import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.ArtistRequestDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import com.acervo.api.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/artistas")
@RequiredArgsConstructor
@Tag(name = "Artistas", description = "Endpoints para gerenciar artistas")
public class ArtistController {

    private final ArtistService service;

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Retorna uma lista paginada de artistas, filtrando por nome e tipo.")
    public Page<ArtistResponseDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ArtistType type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "name") String sort) {

        Pageable pageable;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        } else {
            pageable = Pageable.unpaged(Sort.by(Sort.Direction.ASC, sort));
        }

        return service.findAll(name, type, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar artista", description = "Cria um novo artista.")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ArtistResponseDTO create(@RequestBody @Valid ArtistRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza os dados de um artista existente.")
    @ApiResponse(responseCode = "200", description = "Atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    public ArtistResponseDTO update(@PathVariable Long id, @RequestBody @Valid ArtistRequestDTO dto) {
        return service.update(id, dto);
    }
}
