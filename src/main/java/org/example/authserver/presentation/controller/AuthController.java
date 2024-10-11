package org.example.authserver.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.authserver.application.service.AuthService;
import org.example.authserver.presentation.dto.request.RefreshTokenRequestDto;
import org.example.authserver.presentation.dto.request.TokenRequestDto;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public TokenResponseDto generateTokens(@RequestBody TokenRequestDto tokenRequestDto) {
        return authService.generateTokens(tokenRequestDto.userId());
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        refreshToken = refreshToken.trim();
        return authService.refreshAccessToken(refreshToken);
    }

}