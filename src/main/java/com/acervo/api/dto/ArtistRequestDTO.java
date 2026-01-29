package com.acervo.api.dto;

import com.acervo.api.domain.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotNull(message = "Tipo é obrigatório")
    private ArtistType type;
}
