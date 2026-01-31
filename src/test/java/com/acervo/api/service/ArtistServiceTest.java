package com.acervo.api.service;

import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.ArtistRequestDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import com.acervo.api.mapper.ArtistMapper;
import com.acervo.api.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

        @Mock
        private ArtistRepository artistRepository;

        @Mock
        private ArtistMapper artistMapper;

        @InjectMocks
        private ArtistService artistService;

        @Test
        void create_WithSoloType_ShouldReturnCreatedArtist() {
                // Given
                ArtistRequestDTO request = ArtistRequestDTO.builder()
                                .name("Mike Shinoda")
                                .type(ArtistType.SOLO)
                                .build();

                Artist artist = Artist.builder()
                                .name("Mike Shinoda")
                                .type(ArtistType.SOLO)
                                .build();

                Artist savedArtist = Artist.builder()
                                .id(1L)
                                .name("Mike Shinoda")
                                .type(ArtistType.SOLO)
                                .build();

                ArtistResponseDTO expectedResponse = ArtistResponseDTO.builder()
                                .id(1L)
                                .name("Mike Shinoda")
                                .type(ArtistType.SOLO)
                                .build();

                when(artistMapper.toEntity(request)).thenReturn(artist);
                when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);
                when(artistMapper.toDTO(savedArtist)).thenReturn(expectedResponse);

                // When
                ArtistResponseDTO response = artistService.create(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo(1L);
                assertThat(response.getName()).isEqualTo("Mike Shinoda");
                assertThat(response.getType()).isEqualTo(ArtistType.SOLO);

                verify(artistRepository).save(any(Artist.class));
        }

        @Test
        void create_WithBandType_ShouldReturnCreatedArtist() {
                // Given
                ArtistRequestDTO request = ArtistRequestDTO.builder()
                                .name("Linkin Park")
                                .type(ArtistType.BAND)
                                .build();

                Artist artist = Artist.builder()
                                .name("Linkin Park")
                                .type(ArtistType.BAND)
                                .build();

                Artist savedArtist = Artist.builder()
                                .id(1L)
                                .name("Linkin Park")
                                .type(ArtistType.BAND)
                                .build();

                ArtistResponseDTO expectedResponse = ArtistResponseDTO.builder()
                                .id(1L)
                                .name("Linkin Park")
                                .type(ArtistType.BAND)
                                .build();

                when(artistMapper.toEntity(request)).thenReturn(artist);
                when(artistRepository.save(any(Artist.class))).thenReturn(savedArtist);
                when(artistMapper.toDTO(savedArtist)).thenReturn(expectedResponse);

                // When
                ArtistResponseDTO response = artistService.create(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getType()).isEqualTo(ArtistType.BAND);
        }

        @Test
        void findAll_WithPagination_ShouldReturnPagedResults() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);

                Artist artist1 = Artist.builder()
                                .id(1L)
                                .name("Artist 1")
                                .type(ArtistType.SOLO)
                                .build();

                Artist artist2 = Artist.builder()
                                .id(2L)
                                .name("Artist 2")
                                .type(ArtistType.BAND)
                                .build();

                Page<Artist> artistPage = new PageImpl<>(List.of(artist1, artist2), pageable, 2);

                ArtistResponseDTO dto1 = ArtistResponseDTO.builder()
                                .id(1L)
                                .name("Artist 1")
                                .type(ArtistType.SOLO)
                                .build();
                ArtistResponseDTO dto2 = ArtistResponseDTO.builder()
                                .id(2L)
                                .name("Artist 2")
                                .type(ArtistType.BAND)
                                .build();

                when(artistRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(artistPage);
                when(artistMapper.toDTO(artist1)).thenReturn(dto1);
                when(artistMapper.toDTO(artist2)).thenReturn(dto2);

                // When
                Page<ArtistResponseDTO> result = artistService.findAll(null, null, pageable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        void update_WithValidData_ShouldReturnUpdatedArtist() {
                // Given
                Long artistId = 1L;
                ArtistRequestDTO request = ArtistRequestDTO.builder()
                                .name("Updated Name")
                                .type(ArtistType.SOLO)
                                .build();

                Artist existingArtist = Artist.builder()
                                .id(artistId)
                                .name("Old Name")
                                .type(ArtistType.BAND)
                                .build();

                Artist updatedArtist = Artist.builder()
                                .id(artistId)
                                .name("Updated Name")
                                .type(ArtistType.SOLO)
                                .build();

                ArtistResponseDTO expectedResponse = ArtistResponseDTO.builder()
                                .id(artistId)
                                .name("Updated Name")
                                .type(ArtistType.SOLO)
                                .build();

                when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
                when(artistRepository.save(any(Artist.class))).thenReturn(updatedArtist);
                when(artistMapper.toDTO(updatedArtist)).thenReturn(expectedResponse);

                // When
                ArtistResponseDTO response = artistService.update(artistId, request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getName()).isEqualTo("Updated Name");
                assertThat(response.getType()).isEqualTo(ArtistType.SOLO);

                verify(artistMapper).updateEntityFromDTO(request, existingArtist);
                verify(artistRepository).save(any(Artist.class));
        }

        @Test
        void update_WithNonExistentArtist_ShouldThrowNotFound() {
                // Given
                Long artistId = 999L;
                ArtistRequestDTO request = ArtistRequestDTO.builder()
                                .name("Updated Name")
                                .type(ArtistType.SOLO)
                                .build();

                when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

                // When / Then
                assertThatThrownBy(() -> artistService.update(artistId, request))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Artista não encontrado");

                verify(artistRepository, never()).save(any());
        }
}
