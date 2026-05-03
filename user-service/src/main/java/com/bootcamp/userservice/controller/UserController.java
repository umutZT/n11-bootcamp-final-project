package com.bootcamp.userservice.controller;

import com.bootcamp.userservice.dto.JwtResponse;
import com.bootcamp.userservice.dto.LoginRequest;
import com.bootcamp.userservice.dto.MessageResponse;
import com.bootcamp.userservice.dto.SignupRequest;
import com.bootcamp.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with USER role. Returns success message."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User registered successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Validation failed (invalid email, short password, etc.)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @Operation(
            summary = "Authenticate a user and issue a JWT",
            description = "Validates credentials and returns a signed JWT bearer token used to access protected endpoints."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed (missing fields)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content)
    })
    @SecurityRequirements
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(request));
    }
}
