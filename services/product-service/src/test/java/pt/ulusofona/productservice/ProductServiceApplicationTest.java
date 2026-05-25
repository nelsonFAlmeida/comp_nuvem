package pt.ulusofona.productservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for ProductServiceApplication.
 *
 * <p>Verifies that the Spring Boot application context loads successfully
 * with all required beans and configurations. Kafka auto-configuration
 * is excluded in the test profile to allow context load without a broker.
 */
@SpringBootTest
@ActiveProfiles("test")
class ProductServiceApplicationTest {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context loads successfully
    }
}
