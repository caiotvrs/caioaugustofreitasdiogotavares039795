package com.acervo.api.controller;

import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.AlbumRequestDTO;
import com.acervo.api.dto.AlbumResponseDTO;
import com.acervo.api.service.AlbumService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/albuns")
@RequiredArgsConstructor
@Tag(name = "Álbuns", description = "Endpoints para gerenciar álbuns")
public class AlbumController {

    private final AlbumService service;

    @GetMapping
    @Operation(summary = "Listar álbuns", description = "Retorna uma lista paginada de álbuns, com filtros opcionais.")
    public Page<AlbumResponseDTO> findAll(
            @RequestParam(required = false) String artistName,
            @RequestParam(required = false) ArtistType artistType,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Campo para ordenação (title, year, id)") @RequestParam(required = false, defaultValue = "title") String sort,
            @io.swagger.v3.oas.annotations.Parameter(description = "Direção da ordenação", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {
                    "ASC", "DESC" })) @RequestParam(required = false, defaultValue = "ASC") String direction) {

        // Validar e converter direção
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            sortDirection = Sort.Direction.ASC; // Fallback para ASC se inválido
        }

        Pageable pageable;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        } else {
            // Quando não há paginação, usar página 0 com tamanho "infinito" para garantir
            // ordenação
            pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sortDirection, sort));
        }

        return service.findAll(artistName, artistType, releaseYear, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar álbum", description = "Cria um álbum enviando apenas JSON.")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public AlbumResponseDTO create(@RequestBody @Valid AlbumRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar álbum", description = "Atualiza os dados de um álbum existente.")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "404", description = "Não encontrado")
    public AlbumResponseDTO update(@PathVariable Long id, @RequestBody @Valid AlbumRequestDTO dto) {
        return service.update(id, dto);
    }

    @PostMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capa", description = "Faz upload da capa do álbum.")
    @ApiResponse(responseCode = "200", description = "Capa atualizada")
    @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    public AlbumResponseDTO uploadCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return service.uploadCover(id, file);
    }
}
