package pt.ulusofona.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pt.ulusofona.userservice.dto.UserRequest;
import pt.ulusofona.userservice.dto.UserResponse;
import pt.ulusofona.userservice.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, GlobalExceptionHandler.class})
class UserControllerParameterizedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest(name = "getUserById id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void getUserById_WithValidId_Returns200(long id) throws Exception {
        UserResponse user = new UserResponse(id, "User" + id, "user" + id + "@test.com", LocalDateTime.now(), LocalDateTime.now());
        when(userService.getUserById(id)).thenReturn(user);

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("User" + id));

        verify(userService, times(1)).getUserById(id);
    }

    @ParameterizedTest(name = "getUserById not found id={0}")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107})
    void getUserById_WhenNotFound_Returns400(long id) throws Exception {
        when(userService.getUserById(id)).thenThrow(new RuntimeException("Utilizador não encontrado com ID: " + id));

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));

        verify(userService, times(1)).getUserById(id);
    }

    @ParameterizedTest(name = "deleteUser id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void deleteUser_WithValidId_Returns204(long id) throws Exception {
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(id);
    }

    @ParameterizedTest(name = "getAllUsers empty then size 0")
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void getAllUsers_WhenEmpty_ReturnsEmptyArray(int _unused) throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }
}
