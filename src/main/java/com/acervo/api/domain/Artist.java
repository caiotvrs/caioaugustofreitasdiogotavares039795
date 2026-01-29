package com.acervo.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

// Classe Artist
// Algumas regras de negócio:
// - O nome não pode ser vazio
// - O artista é obrigatório
// - O artista pode ter vários álbuns
// - O artista pode ter vários regionais

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Não pode ser vazio!")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Tipo não pode ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtistType type;

    @ManyToMany(mappedBy = "artists")
    @Builder.Default
    private Set<Album> albums = new HashSet<>();
}
