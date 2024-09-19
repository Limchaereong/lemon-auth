package org.example.authserver.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.authserver.common.jwt.JwtProvider;
import org.example.authserver.presentation.dto.request.TokenRequestDto;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtProvider jwtProvider;

    @PostMapping("/token")
    public TokenResponseDto generateTokens(@RequestBody TokenRequestDto tokenRequestDto) {
        String accessToken = jwtProvider.createAccessJwt(tokenRequestDto.userId(), tokenRequestDto.userRole());
        String refreshToken = jwtProvider.createRefreshJwt();

        return new TokenResponseDto(accessToken, refreshToken);
    }
}