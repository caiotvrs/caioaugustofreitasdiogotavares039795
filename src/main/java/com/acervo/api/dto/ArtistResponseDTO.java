package com.acervo.api.dto;

import com.acervo.api.domain.ArtistType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistResponseDTO {
    private Long id;
    private String name;
    private ArtistType type;
}
