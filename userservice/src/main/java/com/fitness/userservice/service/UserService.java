package com.fitness.userservice.service;

import com.fitness.userservice.dto.*;
import com.fitness.userservice.model.UserRole;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {

    LoginResponse authenticateUser(LoginRequest loginRequest);

    MessageResponse logoutUser(String token);

    MessageResponse deleteUser(String userId);

    List<UserResponse> findUsersByName(String firstName, String lastName);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(String userId, UpdateUserRequest updateRequest);

    List<UserResponse> findUsersByRole(UserRole role);

    MessageResponse changePassword(String userId, ChangePasswordRequest passwordRequest);

    List<UserResponse> getAllUsers(int page, int size);

    UserResponse getUserProfile(String userId);

    UserResponse registerUser(@Valid RegisterRequest registerRequest);
}
