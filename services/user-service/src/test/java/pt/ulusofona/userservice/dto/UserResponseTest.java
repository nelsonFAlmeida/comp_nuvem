package pt.ulusofona.userservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @ParameterizedTest(name = "id={0} name={1} email={2}")
    @CsvSource({
            "1, Alice, alice@test.com",
            "2, Bob, bob@test.com",
            "999, João, joao@ulusofona.pt"
    })
    void constructor_WithValidArgs_SetsFields(long id, String name, String email) {
        UserResponse r = new UserResponse(id, name, email, NOW, NOW);
        assertEquals(id, r.getId());
        assertEquals(name, r.getName());
        assertEquals(email, r.getEmail());
        assertEquals(NOW, r.getCreatedAt());
        assertEquals(NOW, r.getUpdatedAt());
    }

    @Test
    void noArgsConstructor_CreatesInstanceWithNulls() {
        UserResponse r = new UserResponse();
        assertNull(r.getId());
        assertNull(r.getName());
        assertNull(r.getEmail());
        assertNull(r.getCreatedAt());
        assertNull(r.getUpdatedAt());
    }

    @ParameterizedTest(name = "setId {0}")
    @ValueSource(longs = {1, 2, 100, Long.MAX_VALUE})
    void setId_SetsValue(long id) {
        UserResponse r = new UserResponse();
        r.setId(id);
        assertEquals(id, r.getId());
    }

    @ParameterizedTest(name = "setName {0}")
    @ValueSource(strings = {"A", "Bob", "Maria Silva"})
    void setName_SetsValue(String name) {
        UserResponse r = new UserResponse();
        r.setName(name);
        assertEquals(name, r.getName());
    }

    @ParameterizedTest(name = "setEmail {0}")
    @ValueSource(strings = {"a@b.com", "user@domain.pt"})
    void setEmail_SetsValue(String email) {
        UserResponse r = new UserResponse();
        r.setEmail(email);
        assertEquals(email, r.getEmail());
    }

    @Test
    void setCreatedAt_SetsValue() {
        UserResponse r = new UserResponse();
        r.setCreatedAt(NOW);
        assertEquals(NOW, r.getCreatedAt());
    }

    @Test
    void setUpdatedAt_SetsValue() {
        UserResponse r = new UserResponse();
        r.setUpdatedAt(NOW);
        assertEquals(NOW, r.getUpdatedAt());
    }

    @ParameterizedTest(name = "equals same id={0}")
    @ValueSource(longs = {1, 2, 99})
    void equals_SameFields_ReturnsTrue(long id) {
        UserResponse a = new UserResponse(id, "A", "a@a.com", NOW, NOW);
        UserResponse b = new UserResponse(id, "A", "a@a.com", NOW, NOW);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        UserResponse a = new UserResponse(1L, "A", "a@a.com", NOW, NOW);
        UserResponse b = new UserResponse(2L, "A", "a@a.com", NOW, NOW);
        assertNotEquals(a, b);
    }

    @Test
    void equals_Null_ReturnsFalse() {
        UserResponse r = new UserResponse(1L, "A", "a@a.com", NOW, NOW);
        assertNotEquals(null, r);
    }

    @ParameterizedTest(name = "toString contains id={0}")
    @ValueSource(longs = {1, 2, 100})
    void toString_ContainsId(long id) {
        UserResponse r = new UserResponse(id, "A", "a@a.com", NOW, NOW);
        assertTrue(r.toString().contains(String.valueOf(id)));
    }
}
