package com.fitness.userservice.service.impl;

import com.fitness.userservice.dto.MessageResponse;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UpdateUserRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.model.UserRole;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User createMockUser() {
        User user = new User();
        user.setUserId("user-123");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private RegisterRequest createMockRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        return request;
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = createMockRegisterRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.registerUser(request));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserProfile_Success() {
        String userId = "user-123";
        User user = createMockUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserProfile(userId);

        assertNotNull(response);
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserProfile_UserNotFound_ThrowsException() {
        String userId = "non-existent";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserProfile(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void logoutUser_Success() {
        String token = "Bearer jwt-token";
        MessageResponse response = userService.logoutUser(token);
        assertNotNull(response);
        assertEquals("Logged out successfully", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void deleteUser_Success() {
        String userId = "user-123";
        User user = createMockUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        MessageResponse response = userService.deleteUser(userId);

        assertNotNull(response);
        assertEquals("User deleted successfully", response.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        String userId = "non-existent";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void findUsersByName_FirstNameOnly() {
        String firstName = "John";
        List<User> users = List.of(createMockUser());
        when(userRepository.findByFirstNameContainingIgnoreCase(firstName)).thenReturn(users);

        List<UserResponse> responses = userService.findUsersByName(firstName, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findByFirstNameContainingIgnoreCase(firstName);
        verify(userRepository, never()).findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void findUsersByName_FirstNameAndLastName() {
        String firstName = "John";
        String lastName = "Doe";
        List<User> users = List.of(createMockUser());
        when(userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName)).thenReturn(users);

        List<UserResponse> responses = userService.findUsersByName(firstName, lastName);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
    }

    @Test
    void getUserByEmail_Success() {
        String email = "test@example.com";
        User user = createMockUser();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserByEmail(email);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void updateUser_Success() {
        String userId = "user-123";
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setRole(UserRole.ADMIN);

        User existingUser = createMockUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserResponse response = userService.updateUser(userId, updateRequest);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(UserRole.ADMIN, response.getRole());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void findUsersByRole_Success() {
        UserRole role = UserRole.ADMIN;
        List<User> users = List.of(createMockUser());
        when(userRepository.findByRole(role)).thenReturn(users);

        List<UserResponse> responses = userService.findUsersByRole(role);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findByRole(role);
    }

    @Test
    void getAllUsers_Success() {
        int page = 0;
        int size = 10;
        Page<User> userPage = new PageImpl<>(List.of(createMockUser()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        List<UserResponse> responses = userService.getAllUsers(page, size);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }
}
