package com.acervo.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

// Classe Album
// Algumas regras de negócio:
// - O ano deve ser maior que 1900
// - O título não pode ser vazio
// - A capa é opcional
// - O artista é obrigatório
// 

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título não pode ser vazio!")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Não pode ser nulo!")
    @Column(name = "release_year", nullable = false)
    private Integer year;

    @Column(name = "cover_url")
    private String coverUrl;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "album_artist", joinColumns = @JoinColumn(name = "album_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    @Builder.Default
    private Set<Artist> artists = new HashSet<>();

    // Helper method para sync (n sei se vai dar tempo)
    public void addArtist(Artist artist) {
        this.artists.add(artist);
        artist.getAlbums().add(this);
    }

    public void removeArtist(Artist artist) {
        this.artists.remove(artist);
        artist.getAlbums().remove(this);
    }
}
