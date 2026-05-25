package pt.ulusofona.userservice.controller;

import pt.ulusofona.userservice.dto.UserRequest;
import pt.ulusofona.userservice.dto.UserResponse;
import pt.ulusofona.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        UserResponse user1 = new UserResponse(1L, "João Silva", "joao@example.com", 
                LocalDateTime.now(), LocalDateTime.now());
        UserResponse user2 = new UserResponse(2L, "Maria Santos", "maria@example.com", 
                LocalDateTime.now(), LocalDateTime.now());
        List<UserResponse> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[1].name").value("Maria Santos"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Arrange
        UserResponse user = new UserResponse(1L, "João Silva", "joao@example.com", 
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserRequest request = new UserRequest("João Silva", "joao@example.com");
        UserResponse response = new UserResponse(1L, "João Silva", "joao@example.com", 
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"));

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        UserRequest request = new UserRequest("João Silva Atualizado", "joao.novo@example.com");
        UserResponse response = new UserResponse(1L, "João Silva Atualizado", "joao.novo@example.com", 
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva Atualizado"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserRequest.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturn400WithMessage() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new RuntimeException("Utilizador não encontrado com ID: 999"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Utilizador não encontrado com ID: 999"))
                .andExpect(jsonPath("$.status").value("400"));

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void createUser_WhenInvalidRequest_ShouldReturn400WithValidationErrors() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));

        verify(userService, never()).createUser(any(UserRequest.class));
    }
}

