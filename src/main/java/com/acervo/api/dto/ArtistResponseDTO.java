package com.acervo.api.dto;

import com.acervo.api.domain.ArtistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistResponseDTO {
    private Long id;
    private String name;
    private ArtistType type;
}
