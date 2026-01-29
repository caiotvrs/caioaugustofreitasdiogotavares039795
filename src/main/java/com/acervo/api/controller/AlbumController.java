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
@Tag(name = "Álbuns", description = "Endpoints para gerenciamento de Álbuns")
public class AlbumController {

    private final AlbumService service;

    @GetMapping
    @Operation(summary = "Listar álbuns", description = "Listar álbuns com paginação e filtros. Retorna todos se nenhuma página for informada.")
    @ApiResponse(responseCode = "200", description = "Sucesso")
    public Page<AlbumResponseDTO> findAll(
            @RequestParam(required = false) String artistName,
            @RequestParam(required = false) ArtistType artistType,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "title") String sort) {

        Pageable pageable;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        } else {
            pageable = Pageable.unpaged(Sort.by(Sort.Direction.ASC, sort));
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
    @Operation(summary = "Atualizar álbum")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    public AlbumResponseDTO update(@PathVariable Long id, @RequestBody @Valid AlbumRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping(value = "/{id}/capa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload da capa do álbum")
    @ApiResponse(responseCode = "200", description = "Capa atualizada")
    @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    public AlbumResponseDTO uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return service.uploadCover(id, file);
    }
}
