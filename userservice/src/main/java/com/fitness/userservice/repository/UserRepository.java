package com.fitness.userservice.repository;

import com.fitness.userservice.model.User;
import com.fitness.userservice.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

    Optional<User> findByEmail(String email);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    List<User> findByRole(UserRole role);
}
