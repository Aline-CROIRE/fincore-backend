package com.fincore.auth.controller;

import com.fincore.auth.dto.AuthResponse;
import com.fincore.auth.dto.LoginRequest;
import com.fincore.auth.dto.RefreshTokenRequest;
import com.fincore.auth.dto.RegisterRequest;
import com.fincore.auth.service.AuthService;
import com.fincore.common.dto.ApiResponse;
import com.fincore.common.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Endpoints for user registration, authentication, and token refreshing")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user credential")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto registeredUser = authService.register(request);
        return new ResponseEntity<>(ApiResponse.<UserDto>builder()
                .success(true)
                .message("User registered successfully")
                .data(registeredUser)
                .build(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Authentication successful")
                .data(authResponse)
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh expired JWT token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(authResponse)
                .build());
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user account details by User ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserByUserId(@PathVariable String userId) {
        UserDto userDto = authService.getUserByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userDto)
                .build());
    }
}