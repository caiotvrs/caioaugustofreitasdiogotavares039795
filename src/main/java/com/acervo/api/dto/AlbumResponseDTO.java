package com.acervo.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AlbumResponseDTO {
    private Long id;
    private String title;
    private Integer year;
    private String coverUrl;
    private Set<ArtistResponseDTO> artists;
}
