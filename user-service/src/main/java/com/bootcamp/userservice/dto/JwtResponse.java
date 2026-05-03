package com.bootcamp.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public class JwtResponse {

    @Schema(description = "JWT bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String type;

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "ahmet")
    private String username;

    @Schema(description = "Email address", example = "ahmet@example.com")
    private String email;

    @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;

    public JwtResponse(String token, Long id, String username, String email, String role) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
