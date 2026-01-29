package com.acervo.api.mapper;

import com.acervo.api.domain.Album;
import com.acervo.api.domain.Artist;
import com.acervo.api.dto.AlbumRequestDTO;
import com.acervo.api.dto.AlbumResponseDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    private final ArtistMapper artistMapper;

    public AlbumMapper(ArtistMapper artistMapper) {
        this.artistMapper = artistMapper;
    }

    public Album toEntity(AlbumRequestDTO dto) {
        if (dto == null)
            return null;

        return Album.builder()
                .title(dto.getTitle())
                .year(dto.getYear())
                // Artists relations are handled in the service
                .build();
    }

    public AlbumResponseDTO toDTO(Album entity) {
        if (entity == null)
            return null;

        Set<ArtistResponseDTO> artistDTOs = entity.getArtists().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toSet());

        return AlbumResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .year(entity.getYear())
                .coverUrl(entity.getCoverUrl())
                .artists(artistDTOs)
                .build();
    }

    public void updateEntityFromDTO(AlbumRequestDTO dto, Album entity) {
        if (dto == null || entity == null)
            return;

        entity.setTitle(dto.getTitle());
        entity.setYear(dto.getYear());
        // Artists relations are updated in the service
    }
}
