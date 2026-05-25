package pt.ulusofona.userservice.service;

import pt.ulusofona.userservice.dto.UserRequest;
import pt.ulusofona.userservice.dto.UserResponse;
import pt.ulusofona.userservice.model.User;
import pt.ulusofona.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceParameterizedTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // minimal setup for parameterized tests
    }

    @ParameterizedTest(name = "getUserById id={0} returns user")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8})
    void getUserById_WhenExists_ReturnsUser(long id) {
        User user = new User(id, "User" + id, "user" + id + "@test.com", LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("User" + id, result.getName());
        verify(userRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "getUserById id={0} throws when not found")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107, 108})
    void getUserById_WhenNotExists_Throws(long id) {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(id));

        assertTrue(ex.getMessage().contains("" + id));
        verify(userRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "deleteUser id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void deleteUser_WhenExists_Deletes(long id) {
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(id);

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @ParameterizedTest(name = "deleteUser id={0} throws when not found")
    @ValueSource(longs = {200, 201, 202, 203, 204, 205, 206, 207})
    void deleteUser_WhenNotExists_Throws(long id) {
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(id));

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @ParameterizedTest(name = "getAllUsers returns {0} users")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void getAllUsers_ReturnsCorrectSize(int count) {
        List<User> users = new java.util.ArrayList<>();
        for (long i = 0; i < count; i++) {
            User u = new User(i + 1, "U" + i, "u" + i + "@t.com", LocalDateTime.now(), LocalDateTime.now());
            users.add(u);
        }
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(count, result.size());
        verify(userRepository, times(1)).findAll();
    }
}
