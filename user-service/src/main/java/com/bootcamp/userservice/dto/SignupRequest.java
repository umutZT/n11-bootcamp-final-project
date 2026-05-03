package com.bootcamp.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public class SignupRequest {

    @Schema(description = "Unique username", example = "ahmet", minLength = 3, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(description = "Valid email address", example = "ahmet@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Password (min 6 characters)", example = "password123", minLength = 6, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public SignupRequest() {
    }

    public SignupRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
