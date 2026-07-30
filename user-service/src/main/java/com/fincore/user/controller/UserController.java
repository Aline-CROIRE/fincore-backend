package com.fincore.user.controller;

import com.fincore.common.dto.ApiResponse;
import com.fincore.user.dto.CreateProfileRequest;
import com.fincore.user.dto.UpdateProfileRequest;
import com.fincore.user.dto.UserProfileResponse;
import com.fincore.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile API", description = "Endpoints for managing customer profile details")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new customer profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        UserProfileResponse response = userService.createProfile(request);
        return new ResponseEntity<>(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile created successfully")
                .data(response)
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile details by User ID")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfileByUserId(@PathVariable String userId) {
        UserProfileResponse response = userService.getProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update customer profile details")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@PathVariable String userId,
                                                                          @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile updated successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all customer profiles")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllProfiles() {
        List<UserProfileResponse> profiles = userService.getAllProfiles();
        return ResponseEntity.ok(ApiResponse.<List<UserProfileResponse>>builder()
                .success(true)
                .message("All profiles retrieved successfully")
                .data(profiles)
                .build());
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user profile by User ID")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable String userId) {
        userService.deleteProfile(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User profile deleted successfully")
                .build());
    }
}