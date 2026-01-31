package com.acervo.api.service;

import com.acervo.api.dto.LoginRequestDTO;
import com.acervo.api.dto.LoginResponseDTO;
import com.acervo.api.dto.RefreshRequestDTO;
import com.acervo.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${security.test-user.username}")
    private String testUsername;

    @Value("${security.test-user.password}")
    private String testPassword;

    public LoginResponseDTO login(LoginRequestDTO request) {
        // Validação contra credenciais hardcoded
        if (!testUsername.equals(request.username()) || !testPassword.equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(request.username());
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.username());
        long expiresIn = jwtTokenProvider.getAccessTokenExpiration();

        return new LoginResponseDTO(accessToken, refreshToken, expiresIn);
    }

    public LoginResponseDTO refresh(RefreshRequestDTO request) {
        // Valida o refresh token
        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }

        // Extrai username do refresh token e gera novo access token
        String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        long expiresIn = jwtTokenProvider.getAccessTokenExpiration();

        // Mantém o mesmo refresh token
        return new LoginResponseDTO(newAccessToken, request.refreshToken(), expiresIn);
    }
}
