package org.basoup.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"JWT_SECRET=testSecretKeyThatIsLongEnoughToBeStrong123"})
class AuthServerApplicationTests {

    @Test
    void contextLoads() {
    }
}