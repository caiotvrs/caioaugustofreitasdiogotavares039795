package com.acervo.api.mapper;

import com.acervo.api.domain.Artist;
import com.acervo.api.dto.ArtistRequestDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public Artist toEntity(ArtistRequestDTO dto) {
        if (dto == null)
            return null;

        return Artist.builder()
                .name(dto.getName())
                .type(dto.getType())
                .build();
    }

    public ArtistResponseDTO toDTO(Artist entity) {
        if (entity == null)
            return null;

        return ArtistResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .build();
    }

    public void updateEntityFromDTO(ArtistRequestDTO dto, Artist entity) {
        if (dto == null || entity == null)
            return;

        entity.setName(dto.getName());
        entity.setType(dto.getType());
    }
}
