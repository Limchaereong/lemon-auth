package org.example.authserver.common.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    @Value("${jwt.access-token.expiration-ms}")
    public int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-ms}")
    public int refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessJwt(String userId, String userRole) {
        return Jwts.builder()
                .setHeaderParam("typ", "ACCESS_TOKEN")
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .claim("userId", userId)
                .claim("userRole", userRole)
                .setExpiration(Date.from(Instant.now().plusMillis(accessTokenExpiration)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshJwt() {
        return Jwts.builder()
                .setHeaderParam("typ", "REFRESH_TOKEN")
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(refreshTokenExpiration)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}