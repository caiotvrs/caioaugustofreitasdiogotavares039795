package com.acervo.api.service;

import com.acervo.api.domain.Album;
import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.AlbumNotificationDTO;
import com.acervo.api.dto.AlbumRequestDTO;
import com.acervo.api.dto.AlbumResponseDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import com.acervo.api.mapper.AlbumMapper;
import com.acervo.api.repository.AlbumRepository;
import com.acervo.api.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

        @Mock
        private AlbumRepository albumRepository;

        @Mock
        private ArtistRepository artistRepository;

        @Mock
        private AlbumMapper albumMapper;

        @Mock
        private FileStorageService fileStorageService;

        @Mock
        private SimpMessagingTemplate messagingTemplate;

        @InjectMocks
        private AlbumService albumService;

        @Test
        void create_WithValidData_ShouldReturnCreatedAlbumAndNotify() {
                // Given
                Set<Long> artistIds = Set.of(1L);
                AlbumRequestDTO request = AlbumRequestDTO.builder()
                                .title("Test Album")
                                .year(2024)
                                .artistIds(artistIds)
                                .build();

                Artist artist = Artist.builder()
                                .id(1L)
                                .name("Test Artist")
                                .type(ArtistType.SOLO)
                                .build();

                Album album = Album.builder()
                                .title("Test Album")
                                .year(2024)
                                .artists(new HashSet<>(List.of(artist)))
                                .build();

                Album savedAlbum = Album.builder()
                                .id(1L)
                                .title("Test Album")
                                .year(2024)
                                .artists(new HashSet<>(List.of(artist)))
                                .build();

                ArtistResponseDTO artistDTO = ArtistResponseDTO.builder()
                                .id(1L)
                                .name("Test Artist")
                                .type(ArtistType.SOLO)
                                .build();

                AlbumResponseDTO expectedResponse = AlbumResponseDTO.builder()
                                .id(1L)
                                .title("Test Album")
                                .year(2024)
                                .artists(Set.of(artistDTO))
                                .build();

                when(albumMapper.toEntity(request)).thenReturn(album);
                when(artistRepository.findAllById(artistIds)).thenReturn(List.of(artist));
                when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);
                when(albumMapper.toDTO(savedAlbum)).thenReturn(expectedResponse);

                // When
                AlbumResponseDTO response = albumService.create(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo(1L);
                assertThat(response.getTitle()).isEqualTo("Test Album");

                // Verifica que WebSocket foi notificado
                ArgumentCaptor<AlbumNotificationDTO> notificationCaptor = ArgumentCaptor
                                .forClass(AlbumNotificationDTO.class);
                verify(messagingTemplate).convertAndSend(eq("/topic/albums"), notificationCaptor.capture());

                AlbumNotificationDTO notification = notificationCaptor.getValue();
                assertThat(notification.albumId()).isEqualTo(1L);
                assertThat(notification.title()).isEqualTo("Test Album");
                assertThat(notification.releaseYear()).isEqualTo(2024);
                assertThat(notification.message()).isEqualTo("Novo álbum adicionado");

                verify(albumRepository).save(any(Album.class));
                verify(artistRepository).findAllById(artistIds);
        }

        @Test
        void create_WithInvalidArtists_ShouldThrowBadRequest() {
                // Given
                Set<Long> artistIds = Set.of(1L, 2L);
                AlbumRequestDTO request = AlbumRequestDTO.builder()
                                .title("Test Album")
                                .year(2024)
                                .artistIds(artistIds)
                                .build();
                Album album = Album.builder().title("Test Album").year(2024).build();

                when(albumMapper.toEntity(request)).thenReturn(album);
                when(artistRepository.findAllById(artistIds)).thenReturn(List.of(
                                Artist.builder().id(1L).build() // Apenas 1 artista, faltando o ID 2
                ));

                // When / Then
                assertThatThrownBy(() -> albumService.create(request))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Um ou mais artistas não encontrados");

                verify(albumRepository, never()).save(any());
                verify(messagingTemplate, never()).convertAndSend(any(String.class), any(AlbumNotificationDTO.class));
        }

        @Test
        void create_WithEmptyArtists_ShouldCreateAlbumWithoutArtists() {
                // Given
                AlbumRequestDTO request = AlbumRequestDTO.builder()
                                .title("Test Album")
                                .year(2024)
                                .artistIds(new HashSet<>())
                                .build();
                Album album = Album.builder()
                                .title("Test Album")
                                .year(2024)
                                .artists(new HashSet<>())
                                .build();

                Album savedAlbum = Album.builder()
                                .id(1L)
                                .title("Test Album")
                                .year(2024)
                                .artists(new HashSet<>())
                                .build();

                AlbumResponseDTO expectedResponse = AlbumResponseDTO.builder()
                                .id(1L)
                                .title("Test Album")
                                .year(2024)
                                .artists(new HashSet<>())
                                .build();

                when(albumMapper.toEntity(request)).thenReturn(album);
                when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);
                when(albumMapper.toDTO(savedAlbum)).thenReturn(expectedResponse);

                // When
                AlbumResponseDTO response = albumService.create(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getArtists()).isEmpty();

                verify(albumRepository).save(any(Album.class));
                verify(artistRepository, never()).findAllById(any());
                verify(messagingTemplate).convertAndSend(eq("/topic/albums"), any(AlbumNotificationDTO.class));
        }

        @Test
        void update_WithValidData_ShouldReturnUpdatedAlbum() {
                // Given
                Long albumId = 1L;
                Set<Long> artistIds = Set.of(2L);
                AlbumRequestDTO request = AlbumRequestDTO.builder()
                                .title("Updated Album")
                                .year(2025)
                                .artistIds(artistIds)
                                .build();

                Album existingAlbum = Album.builder()
                                .id(albumId)
                                .title("Old Title")
                                .year(2024)
                                .build();

                Artist newArtist = Artist.builder()
                                .id(2L)
                                .name("New Artist")
                                .build();

                Album updatedAlbum = Album.builder()
                                .id(albumId)
                                .title("Updated Album")
                                .year(2025)
                                .artists(new HashSet<>(List.of(newArtist)))
                                .build();

                ArtistResponseDTO artistDTO = ArtistResponseDTO.builder()
                                .id(2L)
                                .name("New Artist")
                                .build();

                AlbumResponseDTO expectedResponse = AlbumResponseDTO.builder()
                                .id(albumId)
                                .title("Updated Album")
                                .year(2025)
                                .artists(Set.of(artistDTO))
                                .build();

                when(albumRepository.findById(albumId)).thenReturn(java.util.Optional.of(existingAlbum));
                when(artistRepository.findAllById(artistIds)).thenReturn(List.of(newArtist));
                when(albumRepository.save(any(Album.class))).thenReturn(updatedAlbum);
                when(albumMapper.toDTO(updatedAlbum)).thenReturn(expectedResponse);

                // When
                AlbumResponseDTO response = albumService.update(albumId, request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getTitle()).isEqualTo("Updated Album");
                assertThat(response.getYear()).isEqualTo(2025);

                verify(albumMapper).updateEntityFromDTO(request, existingAlbum);
                verify(albumRepository).save(any(Album.class));
        }

        @Test
        void update_WithNonExistentAlbum_ShouldThrowNotFound() {
                // Given
                Long albumId = 999L;
                AlbumRequestDTO request = AlbumRequestDTO.builder()
                                .title("Updated Album")
                                .year(2025)
                                .artistIds(Set.of(1L))
                                .build();

                when(albumRepository.findById(albumId)).thenReturn(java.util.Optional.empty());

                // When / Then
                assertThatThrownBy(() -> albumService.update(albumId, request))
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Álbum não encontrado");

                verify(albumRepository, never()).save(any());
        }
}
