package org.example.authserver.application.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.authserver.common.jwt.JwtProvider;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    public TokenResponseDto generateTokens(String userId, String userRole) {
        String accessToken = jwtProvider.createAccessJwt(userId, userRole);
        String refreshToken = jwtProvider.createRefreshJwt();
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public ResponseEntity<TokenResponseDto> refreshAccessToken(String refreshToken, ServerHttpResponse response) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        Claims claims = jwtProvider.getAllClaimsFromToken(refreshToken);
        String userId = claims.get("userId", String.class);
        String userRole = claims.get("userRole", String.class);

        String newAccessToken = jwtProvider.createAccessJwt(userId, userRole);

        String newRefreshToken = jwtProvider.createRefreshJwt();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(86400)
                .build();
        response.getHeaders().add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefreshToken));
    }
}