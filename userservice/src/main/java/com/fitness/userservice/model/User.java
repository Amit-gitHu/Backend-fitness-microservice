package com.fitness.userservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("userId")
    private String userId;

    @Column(unique = true, nullable = false)
    @JsonProperty("email")
    private String email;

    @Column(nullable = false)
    @JsonProperty("password")
    private String password;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @JsonProperty("role")
    private UserRole role = UserRole.USER;

    @JsonProperty("createdAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
