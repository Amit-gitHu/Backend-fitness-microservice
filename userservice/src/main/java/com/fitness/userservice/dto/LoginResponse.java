package com.fitness.userservice.dto;

import com.fitness.userservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginResponse {
    private String token;
    private String userId;
    private String email;
    private UserRole role;
    private LocalDateTime loginTime;
}
