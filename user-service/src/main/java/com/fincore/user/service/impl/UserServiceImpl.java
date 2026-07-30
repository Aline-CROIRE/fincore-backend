package com.fincore.user.service.impl;

import com.fincore.common.exception.BadRequestException;
import com.fincore.common.exception.ResourceNotFoundException;
import com.fincore.user.dto.CreateProfileRequest;
import com.fincore.user.dto.UpdateProfileRequest;
import com.fincore.user.dto.UserProfileResponse;
import com.fincore.user.model.UserProfile;
import com.fincore.user.repository.UserProfileRepository;
import com.fincore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository profileRepository;

    @Override
    public UserProfileResponse createProfile(CreateProfileRequest request) {
        if (profileRepository.existsByUserId(request.getUserId())) {
            throw new BadRequestException("Profile already exists for User ID: " + request.getUserId());
        }

        if (profileRepository.existsByNationalId(request.getNationalId())) {
            throw new BadRequestException("Profile with National ID " + request.getNationalId() + " already exists");
        }

        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserProfile savedProfile = profileRepository.save(profile);
        return mapToResponse(savedProfile);
    }

    @Override
    public UserProfileResponse getProfileByUserId(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for User ID: " + userId));
        return mapToResponse(profile);
    }

    @Override
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for User ID: " + userId));

        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }

        profile.setUpdatedAt(LocalDateTime.now());
        UserProfile updatedProfile = profileRepository.save(profile);
        return mapToResponse(updatedProfile);
    }

    @Override
    public List<UserProfileResponse> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProfile(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for User ID: " + userId));
        profileRepository.delete(profile);
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .nationalId(profile.getNationalId())
                .address(profile.getAddress())
                .status(profile.getStatus())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}