package com.fitness.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String message;
    private LocalDateTime timestamp;

    public MessageResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
