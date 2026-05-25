package pt.ulusofona.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for UserServiceApplication.
 *
 * <p>Verifies that the Spring Boot application context loads successfully
 * with all required beans and configurations.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTest {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context loads successfully
    }
}
