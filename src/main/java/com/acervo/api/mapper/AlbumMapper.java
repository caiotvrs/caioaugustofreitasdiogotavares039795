package com.acervo.api.mapper;

import com.acervo.api.domain.Album;
import com.acervo.api.dto.AlbumRequestDTO;
import com.acervo.api.dto.AlbumResponseDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import com.acervo.api.service.FileStorageService;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    private final ArtistMapper artistMapper;
    private final FileStorageService fileStorageService;

    public AlbumMapper(ArtistMapper artistMapper, FileStorageService fileStorageService) {
        this.artistMapper = artistMapper;
        this.fileStorageService = fileStorageService;
    }

    public Album toEntity(AlbumRequestDTO dto) {
        if (dto == null)
            return null;

        return Album.builder()
                .title(dto.getTitle())
                .year(dto.getYear())
                // As relações de artistas são tratadas no service
                .build();
    }

    public AlbumResponseDTO toDTO(Album entity) {
        if (entity == null)
            return null;

        Set<ArtistResponseDTO> artistDTOs = entity.getArtists().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toSet());

        String presignedCoverUrl = null;
        if (entity.getCoverUrl() != null && !entity.getCoverUrl().isBlank()) {
            presignedCoverUrl = fileStorageService.generatePresignedUrl(entity.getCoverUrl());
        }

        return AlbumResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .year(entity.getYear())
                .coverUrl(presignedCoverUrl)
                .artists(artistDTOs)
                .build();
    }

    public void updateEntityFromDTO(AlbumRequestDTO dto, Album entity) {
        if (dto == null || entity == null)
            return;

        entity.setTitle(dto.getTitle());
        entity.setYear(dto.getYear());
        // As relações de artistas são atualizadas no service
    }
}
