package com.acervo.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AlbumRequestDTO {
    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser maior que 1900")
    private Integer year;

    @NotEmpty(message = "Pelo menos um artista é obrigatório")
    private Set<Long> artistIds;
}
