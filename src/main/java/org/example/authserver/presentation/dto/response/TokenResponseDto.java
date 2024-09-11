package org.example.authserver.presentation.dto.response;

public record TokenResponseDto (String accessToken, String refreshToken){
}
