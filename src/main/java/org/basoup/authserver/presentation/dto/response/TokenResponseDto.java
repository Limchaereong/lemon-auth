package org.basoup.authserver.presentation.dto.response;

public record TokenResponseDto (String accessToken, String refreshToken){
}
