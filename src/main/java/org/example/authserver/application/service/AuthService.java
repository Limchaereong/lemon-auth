package org.example.authserver.application.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.authserver.common.jwt.JwtProvider;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    public TokenResponseDto generateTokens(String userId) {
        String accessToken = jwtProvider.createAccessJwt(userId);
        String refreshToken = jwtProvider.createRefreshJwt();
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public ResponseEntity<TokenResponseDto> refreshAccessToken(String refreshToken) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        Claims claims = jwtProvider.getAllClaimsFromToken(refreshToken);
        String userId = claims.get("userId", String.class);

        String newAccessToken = jwtProvider.createAccessJwt(userId);
        String newRefreshToken = jwtProvider.createRefreshJwt();

        // 새로 발급된 액세스 토큰과 리프레시 토큰을 응답으로 내려줌
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefreshToken));
    }
}