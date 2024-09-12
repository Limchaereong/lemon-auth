package org.example.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"JWT_SECRET=testSecretKeyForJwt"})
class AuthServerApplicationTests {

    @Test
    void contextLoads() {
    }
}