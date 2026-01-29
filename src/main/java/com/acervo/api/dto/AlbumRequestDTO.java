package com.acervo.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumRequestDTO {

    @NotEmpty(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1500, message = "Ano deve ser maior que 1500")
    private Integer year;

    @NotEmpty(message = "Pelo menos um artista é obrigatório")
    private Set<Long> artistIds;
}
