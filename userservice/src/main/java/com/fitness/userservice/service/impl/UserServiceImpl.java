package com.fitness.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.userservice.dto.*;
import com.fitness.userservice.model.User;
import com.fitness.userservice.model.UserRole;
import com.fitness.userservice.repository.UserRepository;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
/*@Transactional*/
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

   /* @Autowired
    private PasswordEncoder passwordEncoder;*/

    public UserResponse registerUser(@Valid RegisterRequest registerRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("Register new user = " + objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(registerRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }

        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new RuntimeException("Email already exists...");

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());

        User savedUser = userRepository.save(user);

        return getUserResponse(savedUser);
    }

    @Override
    public boolean existsByUserId(String userId) {
        log.info("Existing user found : {}", userId);
        return userRepository.existsById(userId);
    }

    private UserResponse getUserResponse(User savedUser) {
        UserResponse userResponse = new UserResponse();

        userResponse.setUserId(savedUser.getUserId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setRole(savedUser.getRole());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return userResponse;
    }

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserResponse(user);
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        /*User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // In a real application, you would generate a JWT token here
        String token = "generated-jwt-token-" + user.getUserId(); // Placeholder

        log.info("User logged in successfully: {}", user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setLoginTime(LocalDateTime.now());

        return response;*/
        return null;
    }

    @Override
    public MessageResponse logoutUser(String token) {
        log.info("User logged out successfully for token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        return new MessageResponse("Logged out successfully");
    }

    @Override
    public MessageResponse deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);

        return new MessageResponse("User deleted successfully");
    }

    @Override
    public List<UserResponse> findUsersByName(String firstName, String lastName) {
        List<User> users;

        if (lastName != null && !lastName.trim().isEmpty()) {
            users = userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
        } else {
            users = userRepository.findByFirstNameContainingIgnoreCase(firstName);
        }

        return users.stream()
                .map(this::getUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return getUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String userId, UpdateUserRequest updateRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info("Updated existing user : {}", userId);
            System.out.println("Register new user = " + objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(updateRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getRole() != null) {
            user.setRole(updateRequest.getRole());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", userId);

        return getUserResponse(updatedUser);
    }

    @Override
    public List<UserResponse> findUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);

        return users.stream()
                .map(this::getUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse changePassword(String userId, ChangePasswordRequest passwordRequest) {
        /*User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
        return new MessageResponse("Password changed successfully");*/
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.getContent()
                .stream()
                .map(this::getUserResponse)
                .collect(Collectors.toList());
    }
}
