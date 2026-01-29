package com.acervo.api.service;

import com.acervo.api.domain.Artist;
import com.acervo.api.domain.ArtistType;
import com.acervo.api.dto.ArtistRequestDTO;
import com.acervo.api.dto.ArtistResponseDTO;
import com.acervo.api.mapper.ArtistMapper;
import com.acervo.api.repository.ArtistRepository;
import com.acervo.api.repository.specs.ArtistSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository repository;
    private final ArtistMapper mapper;

    @Transactional(readOnly = true)
    public Page<ArtistResponseDTO> findAll(String name, ArtistType type, Pageable pageable) {
        Specification<Artist> spec = ArtistSpecs.withFilter(name, type);
        return repository.findAll(spec, pageable).map(mapper::toDTO);
    }

    @Transactional
    public ArtistResponseDTO create(ArtistRequestDTO dto) {
        Artist artist = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(artist));
    }

    @Transactional
    public ArtistResponseDTO update(Long id, ArtistRequestDTO dto) {
        Artist artist = repository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Artista não encontrado com id: " + id));

        mapper.updateEntityFromDTO(dto, artist);
        return mapper.toDTO(repository.save(artist));
    }
}
