package com.acervo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponseDTO {
    private Long id;
    private String title;
    private Integer year;
    private String coverUrl;
    private Set<ArtistResponseDTO> artists;
}
