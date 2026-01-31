package com.acervo.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZw==";
    private static final long ACCESS_TOKEN_EXPIRATION = 60000L; // 1 min
    private static final long REFRESH_TOKEN_EXPIRATION = 120000L; // 2 min

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void generateAccessToken_ShouldReturnValidJWT() {
        // Given
        String username = "admin";

        // When
        String token = jwtTokenProvider.generateAccessToken(username);

        // Then
        assertThat(token).isNotNull()
                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
    }

    @Test
    void generateRefreshToken_ShouldReturnValidJWT() {
        // Given
        String username = "admin";

        // When
        String token = jwtTokenProvider.generateRefreshToken(username);

        // Then
        assertThat(token).isNotNull()
                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtTokenProvider.generateAccessToken("admin");

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() throws InterruptedException {
        // Given - token com expiração de 1ms
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(TEST_SECRET, 1L, 1L);
        String token = shortLivedProvider.generateAccessToken("admin");
        Thread.sleep(10); // Aguarda expirar

        // When
        boolean isValid = shortLivedProvider.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        // Given
        String validToken = jwtTokenProvider.generateAccessToken("admin");
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // When
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        // Given
        String expectedUsername = "admin";
        String token = jwtTokenProvider.generateAccessToken(expectedUsername);

        // When
        String actualUsername = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertThat(actualUsername).isEqualTo(expectedUsername);
    }

    @Test
    void getAccessTokenExpiration_ShouldReturnConfiguredValue() {
        // When
        long expiration = jwtTokenProvider.getAccessTokenExpiration();

        // Then
        assertThat(expiration).isEqualTo(ACCESS_TOKEN_EXPIRATION);
    }

    @Test
    void validateToken_WithNoneAlgorithm_ShouldReturnFalse() {
        // Given - token com alg: "none" (ataque conhecido)
        String noneToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiJhZG1pbiJ9.";

        // When
        boolean isValid = jwtTokenProvider.validateToken(noneToken);

        // Then
        assertThat(isValid).isFalse();
    }
}
