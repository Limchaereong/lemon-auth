package common.jwt;

import io.jsonwebtoken.Claims;
import org.basoup.authserver.common.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // 테스트용 secret key는 최소 32바이트 이상이어야 합니다.
        String secretKey = "testsecretkeytestsecretkeytestsecretkey";
        jwtProvider = new JwtProvider(secretKey);

        // 테스트용으로 액세스 토큰과 리프레시 토큰의 만료 시간을 설정합니다.
        jwtProvider.accessTokenExpiration = 60000; // 60초
        jwtProvider.refreshTokenExpiration = 1209600000; // 14일
    }

    @Test
    void testCreateAccessJwt() {
        String userId = "12345";

        String accessToken = jwtProvider.createAccessJwt(userId);
        assertNotNull(accessToken);
        System.out.println("Access Token: " + accessToken);

        // 액세스 토큰에서 클레임을 추출하여 검증
        Claims claims = jwtProvider.getAllClaimsFromToken(accessToken);
        assertEquals(userId, claims.get("userId"));
    }

    @Test
    void testCreateRefreshJwt() {
        String userId = "12345";

        String refreshToken = jwtProvider.createRefreshJwt(userId); // userId 포함
        assertNotNull(refreshToken);
        System.out.println("Refresh Token: " + refreshToken);

        // 리프레시 토큰도 유효한지 확인
        assertTrue(jwtProvider.validateToken(refreshToken));

        // 리프레시 토큰에서 클레임을 추출하여 검증
        Claims claims = jwtProvider.getAllClaimsFromToken(refreshToken);
        assertEquals(userId, claims.get("userId")); // userId가 포함되었는지 확인
    }

    @Test
    void testValidateToken() {
        String userId = "12345";

        String accessToken = jwtProvider.createAccessJwt(userId);
        assertNotNull(accessToken);

        // 유효한 토큰의 유효성 확인
        boolean isValid = jwtProvider.validateToken(accessToken);
        assertTrue(isValid);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";

        // 잘못된 토큰에 대한 유효성 검사
        boolean isValid = jwtProvider.validateToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        String userId = "12345";

        // 매우 짧은 유효 기간을 가진 토큰 생성
        jwtProvider.accessTokenExpiration = 1000; // 1초
        String accessToken = jwtProvider.createAccessJwt(userId);

        // 바로 생성 직후에는 유효
        assertTrue(jwtProvider.validateToken(accessToken));

        // 토큰 만료를 확인하기 위해 1초 대기
        Thread.sleep(2000); // 2초 대기하여 토큰 만료 유도

        // 만료된 토큰은 유효하지 않음
        assertFalse(jwtProvider.validateToken(accessToken));
    }
}