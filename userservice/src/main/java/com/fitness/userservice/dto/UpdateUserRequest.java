package com.fitness.userservice.dto;

import com.fitness.userservice.model.UserRole;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private UserRole role;
}
