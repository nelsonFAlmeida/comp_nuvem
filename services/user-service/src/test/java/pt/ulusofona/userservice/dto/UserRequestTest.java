package pt.ulusofona.userservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @ParameterizedTest(name = "constructor with name={0}, email={1}")
    @CsvSource({
            "Alice, alice@test.com",
            "Bob, bob@example.org",
            "João Silva, joao@ulusofona.pt",
            "A, a@b.co",
            "Long Name Here, user.name+tag@domain.pt"
    })
    void constructor_WithValidArgs_SetsFields(String name, String email) {
        UserRequest req = new UserRequest(name, email);
        assertEquals(name, req.getName());
        assertEquals(email, req.getEmail());
    }

    @Test
    void noArgsConstructor_CreatesInstanceWithNulls() {
        UserRequest req = new UserRequest();
        assertNull(req.getName());
        assertNull(req.getEmail());
    }

    @ParameterizedTest(name = "setName then getName = {0}")
    @ValueSource(strings = {"Alice", "Bob", "João", "X", "Very Long Name"})
    void setName_SetsValue(String name) {
        UserRequest req = new UserRequest();
        req.setName(name);
        assertEquals(name, req.getName());
    }

    @ParameterizedTest(name = "setEmail then getEmail = {0}")
    @CsvSource({
            "a@b.com",
            "user@domain.pt",
            "test+tag@example.org"
    })
    void setEmail_SetsValue(String email) {
        UserRequest req = new UserRequest();
        req.setEmail(email);
        assertEquals(email, req.getEmail());
    }

    @ParameterizedTest(name = "equals: same name={0} email={1}")
    @CsvSource({
            "A, a@b.com",
            "B, b@b.com",
            "C, c@c.pt"
    })
    void equals_SameFields_ReturnsTrue(String name, String email) {
        UserRequest a = new UserRequest(name, email);
        UserRequest b = new UserRequest(name, email);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_SameInstance_ReturnsTrue() {
        UserRequest req = new UserRequest("A", "a@b.com");
        assertEquals(req, req);
    }

    @Test
    void equals_Null_ReturnsFalse() {
        UserRequest req = new UserRequest("A", "a@b.com");
        assertNotEquals(null, req);
        assertFalse(req.equals(null));
    }

    @ParameterizedTest(name = "equals different: ({0},{1}) vs ({2},{3})")
    @CsvSource({
            "A, a@a.com, A, b@b.com",
            "A, a@a.com, B, a@a.com",
            "X, x@x.pt, Y, y@y.pt"
    })
    void equals_DifferentFields_ReturnsFalse(String n1, String e1, String n2, String e2) {
        UserRequest a = new UserRequest(n1, e1);
        UserRequest b = new UserRequest(n2, e2);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest(name = "toString contains name={0}")
    @ValueSource(strings = {"Alice", "Bob", "Test"})
    void toString_ContainsName(String name) {
        UserRequest req = new UserRequest(name, "e@e.com");
        assertTrue(req.toString().contains(name));
    }

    @ParameterizedTest(name = "hashCode consistent for name={0} email={1}")
    @CsvSource({
            "A, a@a.com",
            "B, b@b.com",
            "C, c@c.com"
    })
    void hashCode_Consistent(String name, String email) {
        UserRequest req = new UserRequest(name, email);
        assertEquals(req.hashCode(), req.hashCode());
    }
}
