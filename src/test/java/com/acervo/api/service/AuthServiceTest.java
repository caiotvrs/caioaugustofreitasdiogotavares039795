package com.acervo.api.service;

import com.acervo.api.dto.LoginRequestDTO;
import com.acervo.api.dto.LoginResponseDTO;
import com.acervo.api.dto.RefreshRequestDTO;
import com.acervo.api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    private static final String ACCESS_TOKEN = "access.token.here";
    private static final String REFRESH_TOKEN = "refresh.token.here";
    private static final long EXPIRATION = 60000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "testUsername", TEST_USERNAME);
        ReflectionTestUtils.setField(authService, "testPassword", TEST_PASSWORD);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(TEST_USERNAME, TEST_PASSWORD);
        when(jwtTokenProvider.generateAccessToken(TEST_USERNAME)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(TEST_USERNAME)).thenReturn(REFRESH_TOKEN);
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(EXPIRATION);

        // When
        LoginResponseDTO response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(response.expiresIn()).isEqualTo(EXPIRATION);

        verify(jwtTokenProvider).generateAccessToken(TEST_USERNAME);
        verify(jwtTokenProvider).generateRefreshToken(TEST_USERNAME);
        verify(jwtTokenProvider).getAccessTokenExpiration();
    }

    @Test
    void login_WithInvalidUsername_ShouldThrowUnauthorized() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("wronguser", TEST_PASSWORD);

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");

        verify(jwtTokenProvider, never()).generateAccessToken(anyString());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowUnauthorized() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(TEST_USERNAME, "wrongpass");

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");

        verify(jwtTokenProvider, never()).generateAccessToken(anyString());
    }

    @Test
    void login_WithNullUsername_ShouldThrowUnauthorized() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(null, TEST_PASSWORD);

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewAccessToken() {
        // Given
        RefreshRequestDTO request = new RefreshRequestDTO(REFRESH_TOKEN);
        when(jwtTokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtTokenProvider.generateAccessToken(TEST_USERNAME)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(EXPIRATION);

        // When
        LoginResponseDTO response = authService.refresh(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN); // Mesmo refresh token
        assertThat(response.expiresIn()).isEqualTo(EXPIRATION);

        verify(jwtTokenProvider).validateToken(REFRESH_TOKEN);
        verify(jwtTokenProvider).getUsernameFromToken(REFRESH_TOKEN);
        verify(jwtTokenProvider).generateAccessToken(TEST_USERNAME);
    }

    @Test
    void refresh_WithInvalidToken_ShouldThrowUnauthorized() {
        // Given
        RefreshRequestDTO request = new RefreshRequestDTO("invalid.token");
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token inválido ou expirado");

        verify(jwtTokenProvider).validateToken("invalid.token");
        verify(jwtTokenProvider, never()).getUsernameFromToken(anyString());
    }

    @Test
    void refresh_WithExpiredToken_ShouldThrowUnauthorized() {
        // Given
        RefreshRequestDTO request = new RefreshRequestDTO(REFRESH_TOKEN);
        when(jwtTokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token inválido ou expirado");
    }
}
