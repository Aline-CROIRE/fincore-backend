package com.fincore.user.dto;

import com.fincore.user.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String nationalId;
    private Address address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}