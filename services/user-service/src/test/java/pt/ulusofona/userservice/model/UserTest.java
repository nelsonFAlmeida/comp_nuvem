package pt.ulusofona.userservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @ParameterizedTest(name = "setId {0}")
    @ValueSource(longs = {1, 2, 100})
    void setId_SetsValue(long id) {
        User u = new User();
        u.setId(id);
        assertEquals(id, u.getId());
    }

    @ParameterizedTest(name = "setName {0}")
    @ValueSource(strings = {"Alice", "Bob", "João"})
    void setName_SetsValue(String name) {
        User u = new User();
        u.setName(name);
        assertEquals(name, u.getName());
    }

    @ParameterizedTest(name = "setEmail {0}")
    @ValueSource(strings = {"a@b.com", "user@domain.pt"})
    void setEmail_SetsValue(String email) {
        User u = new User();
        u.setEmail(email);
        assertEquals(email, u.getEmail());
    }

    @Test
    void setCreatedAt_SetsValue() {
        LocalDateTime t = LocalDateTime.now();
        User u = new User();
        u.setCreatedAt(t);
        assertEquals(t, u.getCreatedAt());
    }

    @Test
    void setUpdatedAt_SetsValue() {
        LocalDateTime t = LocalDateTime.now();
        User u = new User();
        u.setUpdatedAt(t);
        assertEquals(t, u.getUpdatedAt());
    }

    @ParameterizedTest(name = "allArgsConstructor id={0} name={1} email={2}")
    @CsvSource({
            "1, Alice, alice@test.com",
            "2, Bob, bob@test.com"
    })
    void allArgsConstructor_SetsAllFields(long id, String name, String email) {
        LocalDateTime now = LocalDateTime.now();
        User u = new User(id, name, email, now, now);
        assertEquals(id, u.getId());
        assertEquals(name, u.getName());
        assertEquals(email, u.getEmail());
        assertEquals(now, u.getCreatedAt());
        assertEquals(now, u.getUpdatedAt());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        User u = new User();
        assertNull(u.getId());
        assertNull(u.getName());
        assertNull(u.getEmail());
    }

    @Test
    void onCreate_SetsCreatedAtAndUpdatedAt() throws Exception {
        User u = new User();
        u.setName("A");
        u.setEmail("a@a.com");
        u.onCreate();
        assertNotNull(u.getCreatedAt());
        assertNotNull(u.getUpdatedAt());
    }

    @Test
    void onUpdate_SetsUpdatedAt() throws Exception {
        User u = new User(1L, "A", "a@a.com", LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        u.onUpdate();
        assertNotNull(u.getUpdatedAt());
    }

    @ParameterizedTest(name = "equals same id={0}")
    @ValueSource(longs = {1, 2})
    void equals_SameFields_ReturnsTrue(long id) {
        User a = new User(id, "A", "a@a.com", null, null);
        User b = new User(id, "A", "a@a.com", null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        User a = new User(1L, "A", "a@a.com", null, null);
        User b = new User(2L, "A", "a@a.com", null, null);
        assertNotEquals(a, b);
    }
}
