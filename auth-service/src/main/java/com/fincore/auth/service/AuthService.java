package com.fincore.auth.service;

import com.fincore.auth.dto.AuthResponse;
import com.fincore.auth.dto.LoginRequest;
import com.fincore.auth.dto.RefreshTokenRequest;
import com.fincore.auth.dto.RegisterRequest;
import com.fincore.common.dto.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    UserDto getUserByUserId(String userId);
}