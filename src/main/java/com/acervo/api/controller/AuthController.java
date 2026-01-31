package com.acervo.api.controller;

import com.acervo.api.dto.LoginRequestDTO;
import com.acervo.api.dto.LoginResponseDTO;
import com.acervo.api.dto.RefreshRequestDTO;
import com.acervo.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e gerenciamento de tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica usuário e retorna access token (5 min) e refresh token (30 minutos). Credenciais de teste: admin/Seplag@2026")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Renova o access token usando o refresh token. O refresh token permanece o mesmo.")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        LoginResponseDTO response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
