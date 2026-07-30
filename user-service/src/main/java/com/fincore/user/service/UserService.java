package com.fincore.user.service;

import com.fincore.user.dto.CreateProfileRequest;
import com.fincore.user.dto.UpdateProfileRequest;
import com.fincore.user.dto.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse createProfile(CreateProfileRequest request);
    UserProfileResponse getProfileByUserId(String userId);
    UserProfileResponse updateProfile(String userId, UpdateProfileRequest request);
    List<UserProfileResponse> getAllProfiles();
    void deleteProfile(String userId);
}