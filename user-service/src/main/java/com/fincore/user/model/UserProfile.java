package com.fincore.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "profiles")
public class UserProfile {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    @Indexed(unique = true)
    private String nationalId;
    private Address address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}