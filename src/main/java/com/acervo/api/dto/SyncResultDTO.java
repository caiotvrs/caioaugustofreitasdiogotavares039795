package com.acervo.api.dto;

public record SyncResultDTO(
        int inserted,
        int updated,
        int inactivated,
        String message) {
}
