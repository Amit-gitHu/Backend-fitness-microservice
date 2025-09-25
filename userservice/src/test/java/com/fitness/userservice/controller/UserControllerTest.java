package com.fitness.userservice.controller;

import com.fitness.userservice.dto.*;
import com.fitness.userservice.model.UserRole;
import com.fitness.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse createMockUserResponse() {
        UserResponse response = new UserResponse();
        response.setUserId("user-123");
        response.setEmail("test@example.com");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setRole(UserRole.USER);
        return response;
    }

    private LoginResponse createMockLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");
        response.setUserId("user-123");
        response.setEmail("test@example.com");
        response.setRole(UserRole.USER);
        return response;
    }

    @Test
    void getUserProfile_Success() {
        String userId = "user-123";
        UserResponse userResponse = createMockUserResponse();
        when(userService.getUserProfile(userId)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserProfile(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).getUserProfile(userId);
    }

    @Test
    void registerUser_Success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        UserResponse userResponse = createMockUserResponse();
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).registerUser(registerRequest);
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        LoginResponse loginResponse = createMockLoginResponse();
        when(userService.authenticateUser(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = userController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
        verify(userService, times(1)).authenticateUser(loginRequest);
    }

    @Test
    void logout_Success() {
        String token = "Bearer jwt-token";
        MessageResponse messageResponse = new MessageResponse("Logged out successfully");
        when(userService.logoutUser(token)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> response = userController.logout(token);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageResponse, response.getBody());
        verify(userService, times(1)).logoutUser(token);
    }

    @Test
    void deleteUser_Success() {
        String userId = "user-123";
        MessageResponse messageResponse = new MessageResponse("User deleted successfully");
        when(userService.deleteUser(userId)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageResponse, response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void getUsersByName_Success() {
        String firstName = "John";
        String lastName = "Doe";
        List<UserResponse> userResponses = List.of(createMockUserResponse());
        when(userService.findUsersByName(firstName, lastName)).thenReturn(userResponses);

        ResponseEntity<List<UserResponse>> response = userController.getUsersByName(firstName, lastName);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponses, response.getBody());
        verify(userService, times(1)).findUsersByName(firstName, lastName);
    }

    @Test
    void getUserByEmail_Success() {
        String email = "test@example.com";
        UserResponse userResponse = createMockUserResponse();
        when(userService.getUserByEmail(email)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserByEmail(email);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).getUserByEmail(email);
    }

    @Test
    void updateUser_Success() {
        String userId = "user-123";
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Jane");

        UserResponse userResponse = createMockUserResponse();
        when(userService.updateUser(userId, updateRequest)).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.updateUser(userId, updateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService, times(1)).updateUser(userId, updateRequest);
    }

    @Test
    void getUsersByRole_Success() {
        UserRole role = UserRole.ADMIN;
        List<UserResponse> userResponses = List.of(createMockUserResponse());
        when(userService.findUsersByRole(role)).thenReturn(userResponses);
        ResponseEntity<List<UserResponse>> response = userController.getUsersByRole(role);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponses, response.getBody());
        verify(userService, times(1)).findUsersByRole(role);
    }

    @Test
    void changePassword_Success() {
        String userId = "user-123";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest();
        passwordRequest.setCurrentPassword("oldPassword");
        passwordRequest.setNewPassword("newPassword");

        MessageResponse messageResponse = new MessageResponse("Password changed successfully");
        when(userService.changePassword(userId, passwordRequest)).thenReturn(messageResponse);

        ResponseEntity<MessageResponse> response = userController.changePassword(userId, passwordRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageResponse, response.getBody());
        verify(userService, times(1)).changePassword(userId, passwordRequest);
    }

    @Test
    void getAllUsers_Success() {
        int page = 0;
        int size = 10;
        List<UserResponse> userResponses = List.of(createMockUserResponse());
        when(userService.getAllUsers(page, size)).thenReturn(userResponses);

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponses, response.getBody());
        verify(userService, times(1)).getAllUsers(page, size);
    }
}
