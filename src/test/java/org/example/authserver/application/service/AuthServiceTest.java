package org.example.authserver.application.service;

import io.jsonwebtoken.Claims;
import org.example.authserver.common.jwt.JwtProvider;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ServerHttpResponse serverHttpResponse;

    @Mock
    private HttpHeaders httpHeaders;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the HttpHeaders to prevent NullPointerException
        when(serverHttpResponse.getHeaders()).thenReturn(httpHeaders);
    }

    @Test
    void testGenerateTokens() {
        String userId = "user123";
        String userRole = "USER";

        // Mocking JWT 생성
        when(jwtProvider.createAccessJwt(userId, userRole)).thenReturn("mockAccessToken");
        when(jwtProvider.createRefreshJwt()).thenReturn("mockRefreshToken");

        TokenResponseDto tokenResponse = authService.generateTokens(userId, userRole);

        // 검증
        assertNotNull(tokenResponse);
        assertEquals("mockAccessToken", tokenResponse.accessToken());
        assertEquals("mockRefreshToken", tokenResponse.refreshToken());

        // JwtProvider 메서드가 호출되었는지 검증
        verify(jwtProvider).createAccessJwt(userId, userRole);
        verify(jwtProvider).createRefreshJwt();
    }

    @Test
    void testRefreshAccessToken_ValidRefreshToken() {
        String validRefreshToken = "validRefreshToken";
        String userId = "user123";
        String userRole = "USER";

        // Mocking
        when(jwtProvider.isTokenValid(validRefreshToken)).thenReturn(true);
        Claims claims = mock(Claims.class);
        when(claims.get("userId", String.class)).thenReturn(userId);
        when(claims.get("userRole", String.class)).thenReturn(userRole);
        when(jwtProvider.getAllClaimsFromToken(validRefreshToken)).thenReturn(claims);
        when(jwtProvider.createAccessJwt(userId, userRole)).thenReturn("newAccessToken");
        when(jwtProvider.createRefreshJwt()).thenReturn("newRefreshToken");

        // 실행
        ResponseEntity<TokenResponseDto> responseEntity = authService.refreshAccessToken(validRefreshToken, serverHttpResponse);

        // 검증
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        TokenResponseDto tokenResponse = responseEntity.getBody();
        assertNotNull(tokenResponse);
        assertEquals("newAccessToken", tokenResponse.accessToken());
        assertEquals("newRefreshToken", tokenResponse.refreshToken());

        // HttpHeaders 확인 (쿠키 설정)
        verify(httpHeaders).add(eq(HttpHeaders.SET_COOKIE), anyString());
        verify(jwtProvider).createAccessJwt(userId, userRole);
        verify(jwtProvider).createRefreshJwt();
    }

    @Test
    void testRefreshAccessToken_InvalidRefreshToken() {
        String invalidRefreshToken = "invalidRefreshToken";

        // Mocking
        when(jwtProvider.isTokenValid(invalidRefreshToken)).thenReturn(false);

        // 실행
        ResponseEntity<TokenResponseDto> responseEntity = authService.refreshAccessToken(invalidRefreshToken, serverHttpResponse);

        // 검증
        assertNotNull(responseEntity);
        assertEquals(401, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        // JwtProvider 메서드가 호출되지 않음을 확인
        verify(jwtProvider, never()).getAllClaimsFromToken(anyString());
        verify(jwtProvider, never()).createAccessJwt(anyString(), anyString());
        verify(jwtProvider, never()).createRefreshJwt();
    }
}