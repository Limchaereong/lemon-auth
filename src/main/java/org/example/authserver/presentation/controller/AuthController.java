package org.example.authserver.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.authserver.application.service.AuthService;
import org.example.authserver.presentation.dto.request.TokenRequestDto;
import org.example.authserver.presentation.dto.response.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 및 토큰 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "토큰 생성", description = "사용자의 ID를 기반으로 액세스 토큰과 리프레시 토큰을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @PostMapping("/token")
    public TokenResponseDto generateTokens(
        @Parameter(description = "토큰 요청에 필요한 사용자 ID를 포함하는 DTO", required = true)
        @RequestBody TokenRequestDto tokenRequestDto) {
        return authService.generateTokens(tokenRequestDto.userId());
    }

    @Operation(summary = "액세스 토큰 갱신", description = "헤더에 포함된 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @ApiResponse(responseCode = "401", description = "리프레시 토큰이 유효하지 않음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(
        @Parameter(description = "리프레시 토큰", required = true, example = "eyJhbGciOiJIUzI1NiIsIn...")
        @RequestHeader("Authorization") String refreshToken) {
        refreshToken = refreshToken.trim();
        return authService.refreshAccessToken(refreshToken);
    }

}