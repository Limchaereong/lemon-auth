package org.basoup.authserver.application.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import org.basoup.authserver.common.jwt.JwtProvider;
import org.basoup.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    public TokenResponseDto generateTokens(String userId) {
        String accessToken = jwtProvider.createAccessJwt(userId);
        String refreshToken = jwtProvider.createRefreshJwt(userId);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public ResponseEntity<TokenResponseDto> refreshAccessToken(String refreshToken) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        Claims claims = jwtProvider.getAllClaimsFromToken(refreshToken);
        String userId = claims.get("userId", String.class);

        String newAccessToken = jwtProvider.createAccessJwt(userId);
        String newRefreshToken = jwtProvider.createRefreshJwt(userId);

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefreshToken));
    }
}