package com.acervo.api.dto;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        long expiresIn) {
}
