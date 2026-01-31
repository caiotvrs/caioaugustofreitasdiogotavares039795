package com.acervo.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumNotificationDTO(
        Long albumId,
        String title,
        Integer releaseYear,
        List<String> artistNames,
        String message,
        LocalDateTime timestamp) {
}
