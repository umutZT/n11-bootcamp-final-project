package com.bootcamp.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic single-message response")
public class MessageResponse {

    @Schema(description = "Human-readable message", example = "User registered successfully")
    private String message;

    public MessageResponse() {
    }

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
