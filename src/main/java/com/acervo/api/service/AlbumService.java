package com.acervo.api.service;

import com.acervo.api.domain.Album;
import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.AlbumNotificationDTO;
import com.acervo.api.dto.AlbumRequestDTO;
import com.acervo.api.dto.AlbumResponseDTO;
import com.acervo.api.mapper.AlbumMapper;
import com.acervo.api.repository.AlbumRepository;
import com.acervo.api.repository.ArtistRepository;
import com.acervo.api.repository.specs.AlbumSpecs;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumMapper albumMapper;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public Page<AlbumResponseDTO> findAll(String artistName, ArtistType artistType, Integer releaseYear,
            Pageable pageable) {
        return albumRepository.findAll(AlbumSpecs.withFilter(artistName, artistType, releaseYear), pageable)
                .map(albumMapper::toDTO);
    }

    @Transactional
    public AlbumResponseDTO create(AlbumRequestDTO dto) {
        Album album = albumMapper.toEntity(dto);
        Set<Artist> artists = fetchArtists(dto.getArtistIds());
        album.setArtists(artists);

        Album saved = albumRepository.save(album);
        AlbumResponseDTO response = albumMapper.toDTO(saved);

        // Enviar notificação WebSocket
        sendAlbumNotification(saved);

        return response;
    }

    @Transactional
    public AlbumResponseDTO update(Long id, AlbumRequestDTO dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Álbum não encontrado com id: " + id));

        albumMapper.updateEntityFromDTO(dto, album);

        if (dto.getArtistIds() != null) {
            Set<Artist> artists = fetchArtists(dto.getArtistIds());
            album.setArtists(artists);
        }

        return albumMapper.toDTO(albumRepository.save(album));
    }

    @Transactional
    public AlbumResponseDTO uploadCover(Long id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio.");
        }

        // Validação de tamanho (ex: 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "O arquivo excede o tamanho máximo de 5MB.");
        }

        // Validação de tipo (apenas imagens)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Apenas arquivos de imagem são permitidos.");
        }

        Album album = albumRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Álbum não encontrado com id: " + id));

        // Upload retorna a chave (caminho) do arquivo
        String objectKey = fileStorageService.upload(file, "covers");
        album.setCoverUrl(objectKey);

        return albumMapper.toDTO(albumRepository.save(album));
    }

    private Set<Artist> fetchArtists(Set<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Artist> artists = artistRepository.findAllById(artistIds);
        if (artists.size() != artistIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um ou mais artistas não encontrados");
        }
        return new HashSet<>(artists);
    }

    private void sendAlbumNotification(Album album) {
        List<String> artistNames = album.getArtists().stream()
                .map(Artist::getName)
                .collect(Collectors.toList());

        AlbumNotificationDTO notification = new AlbumNotificationDTO(
                album.getId(),
                album.getTitle(),
                album.getYear(),
                artistNames,
                "Novo álbum adicionado",
                LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/albums", notification);
    }
}
