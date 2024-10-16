package org.basoup.authserver.application.service;

import io.jsonwebtoken.Claims;

import org.basoup.authserver.common.jwt.JwtProvider;
import org.basoup.authserver.presentation.dto.response.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTokens_ShouldReturnValidTokens() {
        // Arrange
        String userId = "testUser";
        String userRole = "USER";
        String expectedAccessToken = "access_token";
        String expectedRefreshToken = "refresh_token";

        when(jwtProvider.createAccessJwt(userId)).thenReturn(expectedAccessToken);
        when(jwtProvider.createRefreshJwt(userId)).thenReturn(expectedRefreshToken);

        // Act
        TokenResponseDto tokenResponseDto = authService.generateTokens(userId);

        // Assert
        assertNotNull(tokenResponseDto);
        assertEquals(expectedAccessToken, tokenResponseDto.accessToken());
        assertEquals(expectedRefreshToken, tokenResponseDto.refreshToken());
    }

    @Test
    void refreshAccessToken_WithValidRefreshToken_ShouldReturnNewTokens() {
        // Arrange
        String refreshToken = "valid_refresh_token";
        String userId = "testUser";
        String userRole = "USER";
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";

        Claims claims = mock(Claims.class);
        when(claims.get("userId", String.class)).thenReturn(userId);
        when(claims.get("userRole", String.class)).thenReturn(userRole);

        when(jwtProvider.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtProvider.getAllClaimsFromToken(refreshToken)).thenReturn(claims);
        when(jwtProvider.createAccessJwt(userId)).thenReturn(newAccessToken);
        when(jwtProvider.createRefreshJwt(userId)).thenReturn(newRefreshToken);

        // Act
        ResponseEntity<TokenResponseDto> responseEntity = authService.refreshAccessToken(refreshToken);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        TokenResponseDto tokenResponseDto = responseEntity.getBody();
        assertNotNull(tokenResponseDto);
        assertEquals(newAccessToken, tokenResponseDto.accessToken());
        assertEquals(newRefreshToken, tokenResponseDto.refreshToken());
    }

    @Test
    void refreshAccessToken_WithInvalidRefreshToken_ShouldReturnUnauthorized() {
        // Arrange
        String refreshToken = "invalid_refresh_token";

        when(jwtProvider.isTokenValid(refreshToken)).thenReturn(false);

        // Act
        ResponseEntity<TokenResponseDto> responseEntity = authService.refreshAccessToken(refreshToken);

        // Assert
        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }
}