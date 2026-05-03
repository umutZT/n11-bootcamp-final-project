package com.bootcamp.userservice.service;

import com.bootcamp.userservice.dto.JwtResponse;
import com.bootcamp.userservice.dto.LoginRequest;
import com.bootcamp.userservice.dto.SignupRequest;
import com.bootcamp.userservice.repository.UserRepository;
import com.bootcamp.userservice.security.JwtUtils;
import com.bootcamp.userservice.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — registration and authentication smoke tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("registerUser: rejects duplicate username")
    void register_duplicateUsername_throws() {
        when(userRepository.existsByUsername("ahmet")).thenReturn(true);

        SignupRequest req = new SignupRequest();
        req.setUsername("ahmet");
        req.setEmail("a@b.com");
        req.setPassword("password123");

        assertThatThrownBy(() -> userService.registerUser(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username");
    }

    @Test
    @DisplayName("authenticateUser: returns JWT on valid credentials")
    void authenticate_validCredentials_returnsJwt() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "ahmet", "a@b.com", "hashed", List.of());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(any())).thenReturn("test-jwt-token");

        LoginRequest req = new LoginRequest();
        req.setUsername("ahmet");
        req.setPassword("password123");

        JwtResponse response = userService.authenticateUser(req);

        assertThat(response.getToken()).isEqualTo("test-jwt-token");
        assertThat(response.getUsername()).isEqualTo("ahmet");
    }
}
