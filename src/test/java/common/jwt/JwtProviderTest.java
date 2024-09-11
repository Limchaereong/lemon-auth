package common.jwt;

import org.example.authserver.common.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secretKey = "testsecretkeytestsecretkeytestsecretkey";
        jwtProvider = new JwtProvider(secretKey);

        jwtProvider.accessTokenExpiration = 60000;
        jwtProvider.refreshTokenExpiration = 1209600000;
    }

    @Test
    void testCreateAccessJwt() {
        String userId = "12345";
        String userRole = "USER";

        String accessToken = jwtProvider.createAccessJwt(userId, userRole);
        assertNotNull(accessToken);
        System.out.println("Access Token: " + accessToken);
    }

    @Test
    void testCreateRefreshJwt() {
        String refreshToken = jwtProvider.createRefreshJwt();
        assertNotNull(refreshToken);
        System.out.println("Refresh Token: " + refreshToken);
    }

    @Test
    void testValidateToken() {
        String userId = "12345";
        String userRole = "USER";

        String accessToken = jwtProvider.createAccessJwt(userId, userRole);
        assertNotNull(accessToken);

        boolean isValid = jwtProvider.validateToken(accessToken);
        assertTrue(isValid);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";
        boolean isValid = jwtProvider.validateToken(invalidToken);
        assertFalse(isValid);
    }
}